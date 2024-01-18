package com.example.mall.order.feign;

import com.example.common.utils.R;
import com.example.mall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("mall-cart")
public interface CartFeignService {

    @GetMapping("/getCurrentCartItems")
    R getCurrentCartItem();
}
