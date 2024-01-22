package com.example.mall.product.feign.fallback;

import com.example.common.exception.BizCodeEnum;
import com.example.common.utils.R;
import com.example.mall.product.feign.WareFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class WareFeignServiceFallBack implements WareFeignService {
    @Override
    public R getSkuHasStock(List<Long> skuIds) {
        log.info("熔断方法调用");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }
}
