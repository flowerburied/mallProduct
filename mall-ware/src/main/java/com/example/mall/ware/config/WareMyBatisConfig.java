package com.example.mall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@MapperScan("com.example.mall.ware.dao")
@Configuration
public class WareMyBatisConfig {

    /**
     * 添加分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//        paginationInterceptor.setLimit(500);//设置分页，每页最多500条
//        paginationInterceptor.setOverflow(true);//当请求页码大于最大页码，调回首页
        return paginationInterceptor;
    }
}
