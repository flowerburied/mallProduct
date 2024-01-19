package com.example.mall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class MyRabbitConfig {
    @Resource
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        return jackson2JsonMessageConverter;
    }


    /**
     * 服务器收到消息就回调
     */
    @PostConstruct  //MyRabbitConfig创建完成后，执行这个方法
    public void iitRabbitTemplate() {
//        设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {

            /**
             *
             * @param correlationData （唯一id）
             * @param b （消息是否成功或失败）
             * @param s （失败的原因）
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {

                //服务器收到了
                System.out.println("服务器收到了correlationData==" + correlationData + "==ack==" + b + "==cause==" + s);
            }
        });


        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有投递给指定的队列，就触发这个失败的回调
             * @param message   （那个投递失败消息的详细信息）
             * @param i         （回复的状态码）
             * @param s         （回复的文本内容）
             * @param s1        （发给哪个交换机的）
             * @param s2        （用的哪个路由件）
             */
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                //报错误了，修改数据库当前消息的状态
                System.out.println("message===" + message);
            }
        });
    }

}
