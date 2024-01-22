package com.example.mall.product.feign;

import com.example.common.to.es.SkuEsModel;
import com.example.common.utils.R;
import com.example.mall.product.feign.fallback.SearchFeignServiceFallBack;
import com.example.mall.product.feign.fallback.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "mall-search",fallback = SearchFeignServiceFallBack.class)
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
