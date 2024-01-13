package com.example.mall.order.service.impl;

import com.example.mall.order.entity.OrderEntity;
import com.example.mall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.channels.Channel;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.order.dao.OrderItemDao;
import com.example.mall.order.entity.OrderItemEntity;
import com.example.mall.order.service.OrderItemService;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

//    @RabbitListener(queues = {"hello-java-queue"})
//    public void receiveMessage(Object message, OrderReturnReasonEntity content, Channel channel) {
//        System.out.println("接收到消息。。。内容。。。" + message);
//
//    }
//
//    @RabbitHandler
//    public void receiveMessage2(OrderEntity orderEntity) {
//        System.out.println("接收到消息。。。内容。。。" + orderEntity);
//
//    }

}