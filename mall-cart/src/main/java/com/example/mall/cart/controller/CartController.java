package com.example.mall.cart.controller;

import com.example.mall.cart.interceptor.CartInterceptor;
import com.example.mall.cart.vo.UserInfoTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @GetMapping("/cart.html")
    public String cartListPage() {
        //快速得到 userKey
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart() {

        return "success";
    }
}
