package com.example.mall.seckill.scheduled;


import com.example.mall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class SeckillSkuScheduled {

    @Resource
    SeckillService seckillService;

    //每天晚上三点
    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLate() {
        //重复上架无需处理，因为可以覆盖

        //  秒杀上架功能
        seckillService.uploadSeckillSkuLate();
    }
}
