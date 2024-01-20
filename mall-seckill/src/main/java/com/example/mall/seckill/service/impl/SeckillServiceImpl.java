package com.example.mall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.R;
import com.example.mall.seckill.feign.CouponFeignService;
import com.example.mall.seckill.feign.ProductFeignService;
import com.example.mall.seckill.service.SeckillService;
import com.example.mall.seckill.to.SeckillSkuRedisTo;
import com.example.mall.seckill.vo.SeckillSessionWithSkus;
import com.example.mall.seckill.vo.SeckillSkuVo;
import com.example.mall.seckill.vo.SkuInfoVo;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
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
