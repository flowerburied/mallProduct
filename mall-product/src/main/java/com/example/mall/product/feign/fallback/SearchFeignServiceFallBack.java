package com.example.mall.product.feign.fallback;

import com.example.common.exception.BizCodeEnum;
import com.example.common.to.es.SkuEsModel;
import com.example.common.utils.R;
import com.example.mall.product.feign.SearchFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SearchFeignServiceFallBack implements SearchFeignService {
    @Override
    public R productStatusUp(List<SkuEsModel> skuEsModels) {
        log.info("熔断方法调用");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }
}
