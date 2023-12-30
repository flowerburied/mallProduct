package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.product.dao.AttrAttrgroupRelationDao;
import com.example.mall.product.dao.AttrDao;
import com.example.mall.product.entity.AttrAttrgroupRelationEntity;
import com.example.mall.product.entity.AttrEntity;

import com.example.mall.product.service.AttrAttrgroupRelationService;
import com.example.mall.product.service.AttrService;
import com.example.mall.product.vo.AttrGroupWithAttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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