package com.example.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.product.entity.AttrGroupEntity;
import com.example.mall.product.vo.AttrGroupWithAttrVo;
import com.example.mall.product.vo.skuItemvo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:24:48
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);


    PageUtils queryPageById(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrVo> getGroupWithAttr(Long cateLogId);

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrBySpuId(Long spuId, Long catalogId);
}

