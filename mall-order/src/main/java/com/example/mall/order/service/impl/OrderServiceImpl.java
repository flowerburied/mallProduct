package com.example.mall.order.service.impl;

import com.example.common.vo.MemberRespondVo;
import com.example.mall.order.feign.CartFeignService;
import com.example.mall.order.feign.MemberFeignService;
import com.example.mall.order.interceptor.LoginUserInterceptor;
import com.example.mall.order.vo.MemberAddressVo;
import com.example.mall.order.vo.OrderConfirmVo;
import com.example.mall.order.vo.OrderItemVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.order.dao.OrderDao;
import com.example.mall.order.entity.OrderEntity;
import com.example.mall.order.service.OrderService;

import javax.annotation.Resource;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Resource
    MemberFeignService memberFeignService;

    @Resource
    CartFeignService cartFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 订单确认页返回需要的数据
     *
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespondVo memberRespondVo = LoginUserInterceptor.loginUser.get();
//        查询所以收货地址

        List<MemberAddressVo> address = memberFeignService.getAddress(memberRespondVo.getId());
        confirmVo.setAddresses(address);
        //        查询购物车所选的购物项
        List<OrderItemVo> currentCartItem = cartFeignService.getCurrentCartItem();
        confirmVo.setOrderItems(currentCartItem);
//查询用户积分

        Integer integration = memberRespondVo.getIntegration();
        confirmVo.setIntegral(integration);


        return confirmVo;
    }

}