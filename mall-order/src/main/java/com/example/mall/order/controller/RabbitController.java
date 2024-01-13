package com.example.mall.order.controller;

import com.example.mall.order.entity.OrderEntity;
import com.example.mall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@RestController
public class RabbitController {
    @Resource
    RabbitTemplate rabbitTemplate;


    @GetMapping("/sendMq")
    public String sendMsg(@RequestParam(value = "num", defaultValue = "10") Integer num) {
        for (int i = 0; i < num; i++) {
            if (i % 2 == 0) {

                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
                orderReturnReasonEntity.setId(1L);
                orderReturnReasonEntity.setCreateTime(new Date());
                orderReturnReasonEntity.setName("hh");
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderReturnReasonEntity);

            } else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderEntity);

            }
        }
        return "ok";
    }

}
