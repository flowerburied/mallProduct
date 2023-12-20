package com.example.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:24:48
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

