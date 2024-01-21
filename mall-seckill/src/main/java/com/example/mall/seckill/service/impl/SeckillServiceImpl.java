package com.example.mall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.common.utils.R;
import com.example.common.vo.MemberRespondVo;
import com.example.mall.seckill.feign.CouponFeignService;
import com.example.mall.seckill.feign.ProductFeignService;
import com.example.mall.seckill.interceptor.LoginUserInterceptor;
import com.example.mall.seckill.service.SeckillService;
import com.example.mall.seckill.to.SeckillSkuRedisTo;
import com.example.mall.seckill.vo.SeckillSessionWithSkus;
import com.example.mall.seckill.vo.SeckillSkuVo;
import com.example.mall.seckill.vo.SkuInfoVo;
import io.seata.common.util.CollectionUtils;
import io.seata.common.util.StringUtils;
import jodd.util.CollectionUtil;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Resource
    CouponFeignService couponFeignService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    ProductFeignService productFeignService;
    @Resource
    RedissonClient redissonClient;

    private final String SESSIONS_CACHE_PREFIX = "seckill:session:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";  //商品随机码


    @Override
    public void uploadSeckillSkuLate() {
        //扫描需要参加的活动
        R lateSession = couponFeignService.getLateSession();

        if (lateSession.getCode() == 0) {
            //上架商品
            List<SeckillSessionWithSkus> data = lateSession.getData(new TypeReference<List<SeckillSessionWithSkus>>() {
            });

            //缓存活动信息
            saveSessionInfos(data);
            //缓存活动商品信息
            saveSessionSkuInfos(data);
//            stringRedisTemplate.opsForValue().set();


        }

    }

    //当前时间可以参与的秒杀商品
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {

        //确定当前时间属于哪个秒杀场次
        long time = new Date().getTime();

        Set<String> keys = stringRedisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        for (String key : keys) {
            String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
            String[] s = replace.split("_");
            long start = Long.parseLong(s[0]);
            long end = Long.parseLong(s[1]);
            if (time >= start && time <= end) {
                //获取秒杀场次所有的商品信息
                List<String> range = stringRedisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                List<String> list = hashOps.multiGet(range);

                if (CollectionUtils.isNotEmpty(list)) {
                    List<SeckillSkuRedisTo> collect = list.stream().map(item -> {
                        SeckillSkuRedisTo redisTo = JSON.parseObject(item.toString(), SeckillSkuRedisTo.class);
//                        redisTo.setRandomCode(null);
                        return redisTo;
                    }).collect(Collectors.toList());
                    return collect;
                }
                break;
            }

        }


        //获取这个秒杀场次所有的商品信息
        return Collections.emptyList();

    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        //找到所有需要参与秒杀商品的key
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (CollectionUtils.isNotEmpty(keys)) {
            String regs = "\\d_" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regs, key)) {
                    String json = hashOps.get(key);
                    SeckillSkuRedisTo skuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);

                    //计算时间
                    long current = new Date().getTime();
                    Long startTime = skuRedisTo.getStartTime();
                    Long endTime = skuRedisTo.getEndTime();

                    if (current >= startTime && current <= endTime) {

                    } else {
                        skuRedisTo.setRandomCode(null);
                    }

                    return skuRedisTo;
                }


            }
        }

        return null;
    }

    @Override
    public String kill(String killId, String randomCode, Integer num) {

        MemberRespondVo respondVo = LoginUserInterceptor.loginUser.get();

        //获取秒杀商品的详情数据
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

        String json = hashOps.get(killId);
        if (StringUtils.isEmpty(json)) {
            return null;
        } else {
            SeckillSkuRedisTo redisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
            //校验合法性
            Long startTime = redisTo.getStartTime();
            Long endTime = redisTo.getEndTime();
            long time = new Date().getTime();

            long ttl = endTime - time;

            if (time >= startTime && time <= endTime) {
                //检验随机码和商品ID
                String getRandomCode = redisTo.getRandomCode();
                String skuId = redisTo.getPromotionSessionId() + "_" + redisTo.getSkuId();
                if (getRandomCode.equals(randomCode) && skuId.equals(killId)) {
                    //验证购物数量
                    if (num <= redisTo.getSeckillLimit()) {
                        //验证个人是否已经买过 幂等性处理
                        String redisKey = respondVo.getId() + "_" + skuId;
                        //设置过期时间
                        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);

                        if (aBoolean) {
                            //占位成功
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + getRandomCode);
                            try {
                                boolean b = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                                //秒杀成功
                                //快速下单，发送MQ
                                String timeId = IdWorker.getTimeId();
                                return timeId;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void saveSessionSkuInfos(List<SeckillSessionWithSkus> data) {
        data.forEach(item -> {

            BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);


            item.getRelationSkus().forEach(seckillSkuVo -> {

                String token = UUID.randomUUID().toString().replace("-", "");
//                String keySku = seckillSkuVo.getSkuId().toString();
                String ProSkuKey = seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString();
                String key = SKU_STOCK_SEMAPHORE + token;
                if (!stringRedisTemplate.hasKey(ProSkuKey)) {
                    //緩存商品
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();

                    //sku基本数据
                    R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (skuInfo.getCode() == 0) {
                        SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });

                        redisTo.setSkuInfoVo(info);
                    }
                    //sku秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);

                    //设置时间信息
                    redisTo.setStartTime(item.getStartTime().getTime());
                    redisTo.setEndTime(item.getEndTime().getTime());

                    //商品的随机码
                    redisTo.setRandomCode(token);
                    //缓存活动信息
                    String jsonString = JSON.toJSONString(redisTo);
                    operations.put(ProSkuKey, jsonString);
                    //引入分布式信号量      限流
                    RSemaphore semaphore = redissonClient.getSemaphore(key);
                    //使用商品库存可以秒杀的数量作为信号量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }


            });


        });


    }

    private void saveSessionInfos(List<SeckillSessionWithSkus> data) {
        data.forEach(item -> {
            long startTime = item.getStartTime().getTime();
            long endTime = item.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;

            //缓存活动信息
            Boolean aBoolean = stringRedisTemplate.hasKey(key);
            if (!aBoolean) {
                List<String> collect = item.getRelationSkus()
                        .stream().map(res -> res.getPromotionSessionId() + "_" + res.getSkuId().toString()
                        ).collect(Collectors.toList());
                stringRedisTemplate.opsForList().leftPushAll(key, collect);
            }

        });


    }


}
