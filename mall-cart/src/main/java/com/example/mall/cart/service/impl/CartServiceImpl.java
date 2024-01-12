package com.example.mall.cart.service.impl;

import com.example.mall.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Resource
    StringRedisTemplate stringRedisTemplate;



}
