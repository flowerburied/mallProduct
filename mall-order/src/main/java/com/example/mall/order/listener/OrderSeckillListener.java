package com.example.mall.order.listener;


import com.example.common.to.mq.SeckillOrderTo;
import com.example.mall.order.entity.OrderEntity;
import com.example.mall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@RabbitListener(queues = "order.seckill.order.queue")
@Component
public class OrderSeckillListener {

    @Resource
    OrderService orderService;

    @RabbitHandler
    public void handleRabbitListener(SeckillOrderTo seckillOrderTo, Message message, Channel channel) throws IOException {

        try {
            log.info("收到秒杀信息==" + seckillOrderTo);
            //当前消息是否被第二次及以后（重新）派发
//            Boolean redelivered = message.getMessageProperties().getRedelivered();

            orderService.createSeckillOrder(seckillOrderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }


    }
}
