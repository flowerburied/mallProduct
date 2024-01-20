package com.example.mall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {


    /**
     * 所有对redis的使用都需要RedissonClient对象
     *
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException {
//        创建配置
        Config config = new Config();
//        config.useClusterServers()
//                .addNodeAddress("redis://127.0.0.1:7004", "redis://127.0.0.1:7001");
        config.useSingleServer().setAddress("redis://192.168.18.128:6380");
//        根据config对象创建出RedissonClient示例
        return Redisson.create(config);
    }
}
