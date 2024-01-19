package com.example.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.order.entity.OrderEntity;
import com.example.mall.order.vo.OrderConfirmVo;
import com.example.mall.order.vo.OrderSubmitVo;
import com.example.mall.order.vo.SubmitOrderResponseVo;
import com.example.mall.order.vo.pay.PayVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * ����
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:57:24
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    //下单方法
    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void testFun(String orderSn, String id);

    void closeOrder(OrderEntity orderEntity);

    //获取当前订单是支付消息
    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);
}

