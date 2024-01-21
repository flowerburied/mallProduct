package com.example.mall.order.config;


import com.example.mall.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyMQConfig {


//    @RabbitListener(queues = "order.release.order.queue")
//    public void listener(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
//
//        System.out.println("收到过期的订单信息，准备关闭订单" + orderEntity.getOrderSn());
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//    }

    /**
     * @return
     * @Bean 容器中Binding Queue Exchange 都会自动创建（RabbitMQ没有的情况下）
     */

    @Bean
    public Queue orderDelayQueue() {

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 60000);  // 订单状态改变时间要比库存早
//        String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
        Queue queue = new Queue("order.delay.queue", true, false, false, arguments);

        return queue;
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        Queue queue = new Queue("order.release.order.queue", true, false, false);

        return queue;
    }

    @Bean
    public Exchange orderEventExchange() {
//        String name, boolean durable, boolean autoDelete, Map<String, Object> arguments

        return new TopicExchange("order-event-exchange", true, false);
    }

    @Bean
    public Binding orderCreateOrderBinding() {
//        String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments

        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order-create-order",
                null);

    }

    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }


    /**
     * 订单释放和库存释放进行绑定
     *
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }


    @Bean
    public Queue orderSeckillOrderQueue() {
        return new Queue("order.seckill.order.queue", true, false, false);
    }

    @Bean
    public Binding orderSeckillOrderQueueBinding() {
        return new Binding("stock.seckill.stock.queue",  //目的地
                Binding.DestinationType.QUEUE,  //目的地类型
                "order-event-exchange",     //  绑定的交换机
                "order.release.other.#",    //路由件
                null);
    }

}
