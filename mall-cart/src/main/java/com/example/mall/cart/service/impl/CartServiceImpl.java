package com.example.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.R;
import com.example.mall.cart.feign.ProductFeignService;
import com.example.mall.cart.interceptor.CartInterceptor;
import com.example.mall.cart.service.CartService;
import com.example.mall.cart.vo.CartItem;
import com.example.mall.cart.vo.SkuInfoVo;
import com.example.mall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    ProductFeignService productFeignService;
    @Resource
    ThreadPoolExecutor threadPoolExecutor;

    private static final String CART_PREFIX = "mall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = new CartItem();
        CompletableFuture<Void> skuInfoTask = CompletableFuture.runAsync(() -> {
            //远程查询当前要添加的商品
            R info = productFeignService.info(skuId);
            SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
            });
//        商品添加到购物车

            cartItem.setCheck(true);
            cartItem.setCount(num);
            cartItem.setImage(skuInfo.getSkuDefaultImg());
            cartItem.setTitle(skuInfo.getSkuTitle());
            cartItem.setSkuId(skuId);
            cartItem.setPrice(skuInfo.getPrice());

        }, threadPoolExecutor);

        CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
            //远程查询sku的组合信息
            List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);

            cartItem.setSkuAttrValues(skuSaleAttrValues);
        }, threadPoolExecutor);

        CompletableFuture.allOf(skuInfoTask, getSkuSaleAttrValues).get();
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), s);

        return cartItem;
    }

    //获取到要操作的购物车
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
        return operations;

    }
}
