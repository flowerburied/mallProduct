package com.example.mall.getway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;


@Configuration
public class MallCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfigurationSource configurationSource =new CorsConfigurationSource() {
//            @Override
//            public CorsConfiguration getCorsConfiguration(ServerWebExchange serverWebExchange) {
//                return null;
//            }
//        }
        System.out.println("url===" + source);
        CorsConfiguration corsConfiguration = new CorsConfiguration();

//        Configure cross domain
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowCredentials(true);


        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }
}
