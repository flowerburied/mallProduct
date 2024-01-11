package com.example.mall.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;

@SpringBootTest
class MallAuthServerApplicationTests {

    @Test
    void contextLoads() {
        String code = String.valueOf(new Random().nextInt(100000));
        System.out.println("code==="+code);
    }

}
