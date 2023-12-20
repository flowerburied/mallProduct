package com.example.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:01:27
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

