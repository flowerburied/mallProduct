package com.example.mall.cart.controller;

import com.example.mall.cart.interceptor.CartInterceptor;
import com.example.mall.cart.service.CartService;
import com.example.mall.cart.vo.CartItem;
import com.example.mall.cart.vo.UserInfoTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Resource
    CartService cartService;


    @GetMapping("/cart.html")
    public String cartListPage(Model model) {
        //快速得到 userKey
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        System.out.println("userInfoTo"+userInfoTo);
        model.addAttribute("cart", userInfoTo);
        return "cartlists";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            Model model) throws ExecutionException, InterruptedException {
        CartItem cartItem = cartService.addToCart(skuId, num);

        model.addAttribute("item", cartItem);
        return "success";
    }
}
