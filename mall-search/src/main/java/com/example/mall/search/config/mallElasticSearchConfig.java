package com.example.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class mallElasticSearchConfig {

    @Bean
    public RestHighLevelClient esRestClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
//                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("192.168.18.128", 9201, "http")));
        return client;
    }

}
