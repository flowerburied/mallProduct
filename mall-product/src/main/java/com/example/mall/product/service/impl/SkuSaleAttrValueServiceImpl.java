package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.product.dao.SkuInfoDao;
import com.example.mall.product.entity.SkuInfoEntity;
import com.example.mall.product.vo.skuItemvo.AttrValueWithSkuIdVo;
import com.example.mall.product.vo.skuItemvo.SkuItemSaleAttrsVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.product.dao.SkuSaleAttrValueDao;
import com.example.mall.product.entity.SkuSaleAttrValueEntity;
import com.example.mall.product.service.SkuSaleAttrValueService;

import javax.annotation.Resource;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Resource
    SkuInfoDao skuInfoDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

//            SELECT
//    ssav.attr_id,
//    ssav.attr_name,
//    ssav.attr_value,
//    GROUP_CONCAT(DISTINCT info.sku_id)
//FROM
//    pms_sku_info info
//LEFT JOIN pms_sku_sale_attr_value ssav ON ssav.sku_id = info.sku_id
//WHERE
//    info.spu_id = 7
//GROUP BY
//    ssav.attr_id,
//    ssav.attr_name,
//    ssav.attr_value
    @Override
    public List<SkuItemSaleAttrsVo> getSaleAttrsBySpuId(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = skuInfoDao.selectList(new LambdaQueryWrapper<SkuInfoEntity>().eq(SkuInfoEntity::getSpuId, spuId));

        List<Long> infoIds = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = baseMapper.selectList(new LambdaQueryWrapper<SkuSaleAttrValueEntity>().in(SkuSaleAttrValueEntity::getSkuId, infoIds));

        Map<String, SkuItemSaleAttrsVo> resultMap = new HashMap<>();

        skuSaleAttrValueEntities.forEach(skuSaleAttrValueEntity -> {
            String key = skuSaleAttrValueEntity.getAttrId() + "_" + skuSaleAttrValueEntity.getAttrName() + "_" + skuSaleAttrValueEntity.getAttrValue();

            resultMap.computeIfAbsent(key, k -> {
                SkuItemSaleAttrsVo skuItemSaleAttrsVo = new SkuItemSaleAttrsVo();
                skuItemSaleAttrsVo.setAttrId(skuSaleAttrValueEntity.getAttrId());
                skuItemSaleAttrsVo.setAttrName(skuSaleAttrValueEntity.getAttrName());
                skuItemSaleAttrsVo.setAttrValues(new ArrayList<>());
                return skuItemSaleAttrsVo;
            });

            SkuItemSaleAttrsVo skuItemSaleAttrsVo = resultMap.get(key);
            List<AttrValueWithSkuIdVo> attrValues = skuItemSaleAttrsVo.getAttrValues();

            if (!attrValues.isEmpty()) {
                AttrValueWithSkuIdVo attrValueWithSkuIdVo = attrValues.get(0);
                attrValueWithSkuIdVo.setSkuIds(attrValueWithSkuIdVo.getSkuIds() + "," + skuSaleAttrValueEntity.getSkuId());
            } else {
                AttrValueWithSkuIdVo attrValueWithSkuIdVo = new AttrValueWithSkuIdVo();
                attrValueWithSkuIdVo.setAttrValue(skuSaleAttrValueEntity.getAttrValue());
                attrValueWithSkuIdVo.setSkuIds(String.valueOf(skuSaleAttrValueEntity.getSkuId()));
                attrValues.add(attrValueWithSkuIdVo);
            }
        });

        return new ArrayList<>(resultMap.values());
    }

//    @Override
//    public List<SkuItemSaleAttrsVo> getSaleAttrsBySpuId(Long spuId) {
//        LambdaQueryWrapper<SkuInfoEntity> skuInfoWrapper = new LambdaQueryWrapper<>();
//        skuInfoWrapper.eq(SkuInfoEntity::getSpuId, spuId);
//        List<SkuInfoEntity> skuInfoEntities = skuInfoDao.selectList(skuInfoWrapper);
//        List<Long> infoIds = skuInfoEntities.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
//
//        LambdaQueryWrapper<SkuSaleAttrValueEntity> skuSaleWrapper = new LambdaQueryWrapper<>();
//        skuSaleWrapper.in(SkuSaleAttrValueEntity::getSkuId, infoIds);
//        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = baseMapper.selectList(skuSaleWrapper);
//
//        Map<String, SkuItemSaleAttrsVo> resultMap = new HashMap<>();
//        for (SkuSaleAttrValueEntity skuSaleAttrValueEntity : skuSaleAttrValueEntities) {
//            String key = skuSaleAttrValueEntity.getAttrId() + "_" + skuSaleAttrValueEntity.getAttrName() + "_" + skuSaleAttrValueEntity.getAttrValue();
//            SkuItemSaleAttrsVo skuItemSaleAttrsVo = resultMap.computeIfAbsent(key, k -> new SkuItemSaleAttrsVo());
//
//            skuItemSaleAttrsVo.setAttrId(skuSaleAttrValueEntity.getAttrId());
//            skuItemSaleAttrsVo.setAttrName(skuSaleAttrValueEntity.getAttrName());
//
//            SkuItemSaleAttrsVo orDefault = resultMap.getOrDefault(key, skuItemSaleAttrsVo);
//
//            if (orDefault.getAttrValues() != null && orDefault.getAttrValues().size() > 0) {
//                List<AttrValueWithSkuIdVo> attrValues = orDefault.getAttrValues();
//                AttrValueWithSkuIdVo attrValueWithSkuIdVo = attrValues.get(0);
//                attrValueWithSkuIdVo.setSkuIds(attrValueWithSkuIdVo.getSkuIds() + "," + skuSaleAttrValueEntity.getSkuId().toString());
//            } else {
//                List<AttrValueWithSkuIdVo> list = new ArrayList<>();
//                AttrValueWithSkuIdVo attrValueWithSkuIdVo = new AttrValueWithSkuIdVo();
//                attrValueWithSkuIdVo.setAttrValue(skuSaleAttrValueEntity.getAttrValue());
//                String skuId = skuSaleAttrValueEntity.getSkuId().toString();
//                attrValueWithSkuIdVo.setSkuIds(skuId);
//                list.add(attrValueWithSkuIdVo);
//                orDefault.setAttrValues(list);
//            }
//            resultMap.put(key, orDefault);
//        }
//        List<SkuItemSaleAttrsVo> collect = resultMap.values().stream().collect(Collectors.toList());
//        return collect;
//    }

}