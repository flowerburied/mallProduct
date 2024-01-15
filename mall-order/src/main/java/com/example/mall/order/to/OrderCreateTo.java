package com.example.mall.order.to;

import com.example.mall.order.entity.OrderEntity;
import com.example.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> items;
    private BigDecimal payPrice;
    private BigDecimal fare; // 运费
}
