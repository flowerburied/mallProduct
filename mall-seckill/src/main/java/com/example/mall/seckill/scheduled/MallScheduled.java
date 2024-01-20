package com.example.mall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class MallScheduled {
    /**
     * "* *  *  *  *  ?"
     * 秒 分 时 日 月 钟
     */
    @Async
    @Scheduled(cron = "* * * * * ?")
    public void hello() {
        log.info("hello");
    }
}
