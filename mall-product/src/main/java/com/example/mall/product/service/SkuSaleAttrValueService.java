package com.example.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.product.entity.SkuSaleAttrValueEntity;
import com.example.mall.product.vo.skuItemvo.SkuItemSaleAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * sku
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:24:48
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrsVo> getSaleAttrsBySpuId(Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}

