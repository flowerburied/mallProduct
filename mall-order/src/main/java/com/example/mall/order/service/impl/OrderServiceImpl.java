package com.example.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.R;
import com.example.common.vo.MemberRespondVo;
import com.example.mall.order.feign.CartFeignService;
import com.example.mall.order.feign.MemberFeignService;
import com.example.mall.order.feign.WmsFeignService;
import com.example.mall.order.interceptor.LoginUserInterceptor;
import com.example.mall.order.vo.MemberAddressVo;
import com.example.mall.order.vo.OrderConfirmVo;
import com.example.mall.order.vo.OrderItemVo;
import com.example.mall.order.vo.SkuStockVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.order.dao.OrderDao;
import com.example.mall.order.entity.OrderEntity;
import com.example.mall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Resource
    MemberFeignService memberFeignService;

    @Resource
    CartFeignService cartFeignService;
    @Resource
    ThreadPoolExecutor threadPoolExecutor;
    @Resource
    WmsFeignService wmsFeignService;


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
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespondVo memberRespondVo = LoginUserInterceptor.loginUser.get();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> addressThread = CompletableFuture.runAsync(() -> {
//设置线程
            System.out.println("address线程===" + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //        查询所以收货地址
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespondVo.getId());
            confirmVo.setAddresses(address);
        }, threadPoolExecutor);

        CompletableFuture<Void> cartThread = CompletableFuture.runAsync(() -> {
            //设置线程
            System.out.println("cart线程===" + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //        查询购物车所选的购物项
            List<OrderItemVo> currentCartItem = cartFeignService.getCurrentCartItem();
            confirmVo.setOrderItems(currentCartItem);
        }, threadPoolExecutor).thenRunAsync(() -> {
            List<OrderItemVo> orderItems = confirmVo.getOrderItems();
            List<Long> collect = orderItems.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R skuHasStock = wmsFeignService.getSkuHasStock(collect);
            List<SkuStockVo> data = skuHasStock.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null) {
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);

            }

        }, threadPoolExecutor);


        //查询用户积分
        Integer integration = memberRespondVo.getIntegration();
        confirmVo.setIntegral(integration);

        CompletableFuture.allOf(addressThread, cartThread).get();

        return confirmVo;
    }

}