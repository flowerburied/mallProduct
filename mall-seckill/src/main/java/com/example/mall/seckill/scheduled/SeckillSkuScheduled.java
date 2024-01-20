package com.example.mall.seckill.scheduled;


import com.example.mall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SeckillSkuScheduled {

    @Resource
    SeckillService seckillService;

    @Resource
    RedissonClient redissonClient;

    public final String UPLOAD_LOCK = "seckill:upload:lock";


    //每天晚上三点
    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLate() {
        //重复上架无需处理，因为可以覆盖
        log.info("上架秒杀的商品信息");
        //分布式锁的运用
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            //  秒杀上架功能
            seckillService.uploadSeckillSkuLate();
            lock.unlock();
        } finally {
            lock.unlock();
        }

    }
}
