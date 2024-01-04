package com.example.common.to;

import lombok.Data;

@Data
public class SkuHasStockVo {

    private Long skuId;
    private Boolean stock;

    public Long getSkuId() {
        // 返回类型应为 Long
        return skuId;
    }

    public Boolean getStock() {
        // 返回类型应为 Boolean
        return stock;
    }
}
