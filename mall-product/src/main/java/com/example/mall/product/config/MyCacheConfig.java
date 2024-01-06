package com.example.mall.product.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableConfigurationProperties(CacheProperties.class)
@EnableCaching
@Configuration
public class MyCacheConfig {


    /**
     * 配置文件的配置没有用上
     * 1. 原来和配置文件绑定的配置类为：@ConfigurationProperties(prefix = "spring.cache")
     * public class CacheProperties
     * 2. 要让他生效，要加上 @EnableConfigurationProperties(CacheProperties.class)
     */
    @Bean
    RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {

        RedisCacheConfiguration redisConfig = RedisCacheConfiguration.defaultCacheConfig();


//        redisConfig = redisConfig.entryTtl();
        redisConfig = redisConfig.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        redisConfig = redisConfig.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
//将配置文件中所有的配置都生效
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            redisConfig = redisConfig.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            redisConfig = redisConfig.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            redisConfig = redisConfig.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            redisConfig = redisConfig.disableKeyPrefix();
        }

        return redisConfig;

    }
}
