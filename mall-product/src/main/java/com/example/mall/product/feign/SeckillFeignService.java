package com.example.mall.product.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("mall-seckill")
public interface SeckillFeignService {

    @GetMapping("/sku/seckill/{skuId}")
    @ResponseBody
    R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
