package com.example.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 14:00:18
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

