package com.example.common.to.mq;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class StockLockedTo {
    private Long id; // 库存的工作单
    private StockDetailTo detail; // 详情工作单id
}
