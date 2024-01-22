package com.example.mall.product.feign;

import com.example.common.to.SkuHasStockVo;
import com.example.common.utils.R;
import com.example.mall.product.feign.fallback.SeckillFeignServiceFallBack;
import com.example.mall.product.feign.fallback.WareFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "mall-ware", fallback = WareFeignServiceFallBack.class)
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
