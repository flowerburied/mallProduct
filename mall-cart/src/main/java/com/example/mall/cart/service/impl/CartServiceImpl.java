package com.example.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.R;
import com.example.mall.cart.feign.ProductFeignService;
import com.example.mall.cart.interceptor.CartInterceptor;
import com.example.mall.cart.service.CartService;
import com.example.mall.cart.vo.Cart;
import com.example.mall.cart.vo.CartItem;
import com.example.mall.cart.vo.SkuInfoVo;
import com.example.mall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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


        String res = (String) cartOps.get(skuId.toString());

        if (StringUtils.isEmpty(res)) {
//            购物车无商品
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
        } else {

//            购物车有此商品
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);

            return cartItem;
        }


    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String o = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(o, CartItem.class);
        return cartItem;

    }

    //获取购物车
    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

        if (userInfoTo.getUserId() != null) {
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
//          登录
            List<CartItem> cartItems = getCartItems(tempCartKey);
            if (cartItems != null) {
                //临时购物车需要合并
                for (CartItem item : cartItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                //清空购物车
                clearCart(tempCartKey);

            }
//            获取登录后的购物车 （获取合并了的购物车）
            List<CartItem> cartItems1 = getCartItems(cartKey);
            cart.setItems(cartItems1);
            return cart;

        } else {
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
//            没登录
//            临时购物车
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
            return cart;
        }


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

    private List<CartItem> getCartItems(String cartKey) {


        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);

        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItem> collect = values.stream().map(obj -> {
                String str = (String) obj;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    @Override
    public void clearCart(String cartkey) {

        stringRedisTemplate.delete(cartkey);
    }

    //勾选购物项
    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1 ? true : false);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), s);

    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));

    }

    @Override
    public void deleteIdCartInfo(Integer skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        cartOps.delete(skuId.toString());
    }
}
