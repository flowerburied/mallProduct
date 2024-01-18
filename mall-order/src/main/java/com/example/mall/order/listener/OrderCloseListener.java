package com.example.mall.order.listener;

import com.example.common.to.mq.StockLockedTo;
import com.example.mall.order.entity.OrderEntity;
import com.example.mall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {

    @Resource
    OrderService orderService;

    @RabbitHandler
    public void handleRabbitListener(OrderEntity orderEntity, Message message, Channel channel) throws IOException {
        System.out.println("收到过期订单==" + orderEntity);
        try {
            orderService.closeOrder(orderEntity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }


    }
}
