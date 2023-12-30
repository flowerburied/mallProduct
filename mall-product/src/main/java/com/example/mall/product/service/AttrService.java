package com.example.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.product.entity.AttrEntity;
import com.example.mall.product.vo.AttrRespondVo;
import com.example.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:24:48
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    AttrRespondVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    PageUtils queryPageById(Map<String, Object> param, Long catId, String attrType);


    List<AttrEntity> getRelationAttr(Long attrGroupId);

    void deleteRealtion(List<AttrRespondVo> vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId);
}

