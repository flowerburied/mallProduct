package com.example.mall.order.vo;

import com.example.mall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code = 0; //0成功   状态码
}
