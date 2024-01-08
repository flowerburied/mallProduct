package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.product.dao.AttrAttrgroupRelationDao;
import com.example.mall.product.dao.AttrDao;
import com.example.mall.product.dao.ProductAttrValueDao;
import com.example.mall.product.entity.AttrAttrgroupRelationEntity;
import com.example.mall.product.entity.AttrEntity;

import com.example.mall.product.entity.ProductAttrValueEntity;
import com.example.mall.product.service.AttrAttrgroupRelationService;
import com.example.mall.product.service.AttrService;
import com.example.mall.product.vo.AttrGroupWithAttrVo;
import com.example.mall.product.vo.skuItemvo.SpuItemAttrGroupVo;
import com.example.mall.product.vo.spusavevo.Attr;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.product.dao.AttrGroupDao;
import com.example.mall.product.entity.AttrGroupEntity;
import com.example.mall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Resource
    AttrDao attrDao;
    @Resource
    AttrService attrService;
    @Resource
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Resource
    ProductAttrValueDao productAttrValueDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageById(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");

        LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();

        System.out.println("key===" + key + "===" + catelogId);
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                obj.eq(AttrGroupEntity::getCatelogId, key).or().like(AttrGroupEntity::getAttrGroupName, key);
            });
        }


        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        } else {
            wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrGroupWithAttrVo> getGroupWithAttr(Long cateLogId) {

        // Step 1: Query for AttrGroupEntity
        LambdaQueryWrapper<AttrGroupEntity> attrGroupWrapper = new LambdaQueryWrapper<>();
        attrGroupWrapper.eq(AttrGroupEntity::getCatelogId, cateLogId);
        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(attrGroupWrapper);

        // Step 2: Collect AttrGroupWithAttrVo list
        List<AttrGroupWithAttrVo> result = attrGroupEntities.stream()
                .map(this::convertToAttrGroupWithAttrVo)
                .collect(Collectors.toList());
        return result;
    }

//            SELECT
//                ag.`attr_group_name`,
//                aar.`attr_id`,
//                attr.`attr_name`,
//                pav.`attr_value`
//        FROM `pms_attr_group` ag
//        LEFT JOIN `pms_attr_attrgroup_relation` aar ON aar.`attr_group_id` = ag.`attr_group_id`
//        LEFT JOIN `pms_attr` attr ON attr.`attr_id` = aar.`attr_id`
//        LEFT JOIN `pms_product_attr_value` pav ON pav.`attr_id` = attr.`attr_id`
//        WHERE ag.catelog_id = 225 AND pav.`spu_id` = 7

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrBySpuId(Long spuId, Long catalogId) {

        List<SpuItemAttrGroupVo> result = new ArrayList<>();

        LambdaQueryWrapper<AttrGroupEntity> attrGroupWrappers = new LambdaQueryWrapper<>();
        attrGroupWrappers.eq(AttrGroupEntity::getCatelogId, catalogId);
        List<AttrGroupEntity> ag = baseMapper.selectList(attrGroupWrappers);

        for (AttrGroupEntity attrGroupEntity : ag) {
            List<Attr> attrList = new ArrayList<>();
            SpuItemAttrGroupVo spuItemAttrGroupVo = new SpuItemAttrGroupVo();
            spuItemAttrGroupVo.setGroupName(attrGroupEntity.getAttrGroupName());

            LambdaQueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupWrapper = new LambdaQueryWrapper<>();
            attrAttrgroupWrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupEntity.getAttrGroupId());
            List<AttrAttrgroupRelationEntity> aar = attrAttrgroupRelationDao.selectList(attrAttrgroupWrapper);


            for (AttrAttrgroupRelationEntity aarEntity : aar) {

                LambdaQueryWrapper<AttrEntity> attrWrapper = new LambdaQueryWrapper<>();
                attrWrapper.eq(AttrEntity::getAttrId, aarEntity.getAttrId());
                List<AttrEntity> attr = attrDao.selectList(attrWrapper);

                for (AttrEntity attrentity : attr) {
                    LambdaQueryWrapper<ProductAttrValueEntity> productWrapper = new LambdaQueryWrapper<>();
                    productWrapper.eq(ProductAttrValueEntity::getAttrId, attrentity.getAttrId()).eq(ProductAttrValueEntity::getSpuId, spuId);
                    List<ProductAttrValueEntity> pav = productAttrValueDao.selectList(productWrapper);

                    for (ProductAttrValueEntity productEntity : pav) {
                        Attr attr1 = new Attr();
                        attr1.setAttrId(aarEntity.getAttrId());
                        attr1.setAttrName(attrentity.getAttrName());
                        attr1.setAttrValue(productEntity.getAttrValue());

                        attrList.add(attr1);
                    }
                }
            }

            spuItemAttrGroupVo.setAttrValues(attrList);

            result.add(spuItemAttrGroupVo);
        }
        // 清理attrValues为[]的项
        List<SpuItemAttrGroupVo> cleanedList = result.stream()
                .filter(item -> !item.getAttrValues().isEmpty())
                .collect(Collectors.toList());

        return cleanedList;
    }

//    @Override
//    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrBySpuIds(Long spuId, Long catalogId) {
//        // Step 1: Get Attribute Groups for the Catalog
//        LambdaQueryWrapper<AttrGroupEntity> attrGroupWrapper = new LambdaQueryWrapper<>();
//        attrGroupWrapper.eq(AttrGroupEntity::getCatelogId, catalogId);
//        List<AttrGroupEntity> attrGroups = baseMapper.selectList(attrGroupWrapper);
//
//        // Step 2: Extract Attribute Group IDs
//        List<Long> attrGroupIds = attrGroups.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
//
//        // Step 3: Get Attribute-Attribute Group Relations
//        LambdaQueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupWrapper = new LambdaQueryWrapper<>();
//        attrAttrgroupWrapper.in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds);
//        List<AttrAttrgroupRelationEntity> attrAttrgroupRelations = attrAttrgroupRelationDao.selectList(attrAttrgroupWrapper);
//
//        // Step 4: Extract Attribute IDs
//        List<Long> attrIds = attrAttrgroupRelations.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
//
//        // Step 5: Get Attributes
//        LambdaQueryWrapper<AttrEntity> attrWrapper = new LambdaQueryWrapper<>();
//        attrWrapper.in(AttrEntity::getAttrId, attrIds);
//        List<AttrEntity> attributes = attrDao.selectList(attrWrapper);
//
//        // Step 6: Get Product Attribute Values for the given spuId
//        LambdaQueryWrapper<ProductAttrValueEntity> productAttrValueWrapper = new LambdaQueryWrapper<>();
//        productAttrValueWrapper.in(ProductAttrValueEntity::getAttrId, attrIds);
//        productAttrValueWrapper.eq(ProductAttrValueEntity::getSpuId, spuId);
//        List<ProductAttrValueEntity> productAttrValues = productAttrValueDao.selectList(productAttrValueWrapper);
//
//        // Step 7: Build the Result
//        List<SpuItemAttrGroupVo> result = new ArrayList<>();
//        for (AttrGroupEntity attrGroup : attrGroups) {
//            SpuItemAttrGroupVo spuItemAttrGroupVo = new SpuItemAttrGroupVo();
//            spuItemAttrGroupVo.setAttrGroupId(attrGroup.getAttrGroupId());
//            spuItemAttrGroupVo.setAttrGroupName(attrGroup.getAttrGroupName());
//
//            List<AttrEntity> groupAttributes = attributes.stream()
//                    .filter(attr -> attrAttrgroupRelations.stream()
//                            .anyMatch(aar -> aar.getAttrGroupId().equals(attrGroup.getAttrGroupId())
//                                    && aar.getAttrId().equals(attr.getAttrId())))
//                    .collect(Collectors.toList());
//
//            List<SpuItemAttrVo> attrVos = new ArrayList<>();
//            for (AttrEntity attribute : groupAttributes) {
//                SpuItemAttrVo spuItemAttrVo = new SpuItemAttrVo();
//                spuItemAttrVo.setAttrId(attribute.getAttrId());
//                spuItemAttrVo.setAttrName(attribute.getAttrName());
//
//                List<ProductAttrValueEntity> attrValues = productAttrValues.stream()
//                        .filter(pav -> pav.getAttrId().equals(attribute.getAttrId()))
//                        .collect(Collectors.toList());
//
//                spuItemAttrVo.setAttrValues(attrValues.stream().map(ProductAttrValueEntity::getAttrValue).collect(Collectors.toList()));
//                attrVos.add(spuItemAttrVo);
//            }
//
//            spuItemAttrGroupVo.setAttrs(attrVos);
//            result.add(spuItemAttrGroupVo);
//        }
//
//        return result;
//    }


    private AttrGroupWithAttrVo convertToAttrGroupWithAttrVo(AttrGroupEntity attrGroupEntity) {
        AttrGroupWithAttrVo attrGroupWithAttrVo = new AttrGroupWithAttrVo();
        BeanUtils.copyProperties(attrGroupEntity, attrGroupWithAttrVo);

        // Step 3: Query for AttrAttrgroupRelationEntity and AttrEntity
//        LambdaQueryWrapper<AttrAttrgroupRelationEntity> attrRelationWrapper = new LambdaQueryWrapper<>();
//        attrRelationWrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupEntity.getAttrGroupId());
//        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(attrRelationWrapper);
//
//        List<AttrEntity> attrEntities = attrAttrgroupRelationEntities.stream()
//                .map(attrRelationEntity -> {
//                    LambdaQueryWrapper<AttrEntity> attrWrapper = new LambdaQueryWrapper<>();
//                    attrWrapper.eq(AttrEntity::getAttrId, attrRelationEntity.getAttrId());
//                    return attrDao.selectList(attrWrapper);
//                })
//                .flatMap(List::stream)
//                .collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrService.getRelationAttr(attrGroupEntity.getAttrGroupId());

        attrGroupWithAttrVo.setAttrs(attrEntities);

        return attrGroupWithAttrVo;
    }


}