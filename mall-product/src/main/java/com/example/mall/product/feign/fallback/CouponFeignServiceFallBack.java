package com.example.mall.product.feign.fallback;

import com.example.common.exception.BizCodeEnum;
import com.example.common.to.SkuReductionTo;
import com.example.common.to.SpuBoundTo;
import com.example.common.utils.R;
import com.example.mall.product.feign.CouponFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CouponFeignServiceFallBack implements CouponFeignService {
    @Override
    public R saveSpuBound(SpuBoundTo spuBoundTo) {
        log.info("熔断方法调用");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }

    @Override
    public R saveSkuReduction(SkuReductionTo skuReductionTo) {
        log.info("熔断方法调用");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }
}
