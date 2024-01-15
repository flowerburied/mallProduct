package com.example.mall.ware.vo;

import lombok.Data;

@Data
public class LockStockResult {
    private Long skuId;
    private Integer lockNum;
    private Boolean locked;
}
