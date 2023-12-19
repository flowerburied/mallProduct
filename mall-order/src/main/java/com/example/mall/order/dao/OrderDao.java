package com.example.mall.order.dao;

import com.example.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * ����
 * 
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:57:24
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
