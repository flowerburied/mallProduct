package com.example.mall.ware.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitConfig {


    //使用JSON序列化进行消息的转换
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

//    @RabbitListener(queues = "stock.release.stock.queue")
//    public void handleRabbitListener(Message message) {
//
//    }

    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange("stock-event-exchange", true, false);
    }

    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue("stock.release.stock.queue",
                true, false, false);
    }

    @Bean
    public Queue stockDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        arguments.put("x-dead-letter-routing-key", "stock.release.stock");
        arguments.put("x-message-ttl", 120000);
        return new Queue("stock.delay.queue",
                true,
                false,
                false,
                arguments);
    }

    @Bean
    public Binding stockLockBinding() {
        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.lock.stock",
                null);
    }

    @Bean
    public Binding stockReleaseBinding() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",
                null);
    }

}
