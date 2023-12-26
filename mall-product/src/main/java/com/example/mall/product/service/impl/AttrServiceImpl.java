package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.common.constant.ProductConstant;
import com.example.mall.product.dao.AttrAttrgroupRelationDao;
import com.example.mall.product.dao.AttrGroupDao;
import com.example.mall.product.dao.CategoryDao;
import com.example.mall.product.entity.AttrAttrgroupRelationEntity;
import com.example.mall.product.entity.AttrGroupEntity;
import com.example.mall.product.entity.CategoryEntity;
import com.example.mall.product.service.CategoryService;
import com.example.mall.product.vo.AttrRespondVo;
import com.example.mall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.product.dao.AttrDao;
import com.example.mall.product.entity.AttrEntity;
import com.example.mall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {


    @Resource
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Resource
    AttrGroupDao attrGroupDao;

    @Resource
    CategoryDao categoryDao;

    @Resource
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());

            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }


    }

    @Override
    public PageUtils queryPageById(Map<String, Object> param, Long catId, String attrType) {
        String key = (String) param.get("key");
        LambdaQueryWrapper<AttrEntity> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper.eq(AttrEntity::getAttrType,
                "base".equalsIgnoreCase(attrType) ?
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() :
                        ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        if (catId != 0) {
            objectLambdaQueryWrapper.eq(AttrEntity::getCatelogId, catId);
        }

        if (!StringUtils.isEmpty(key)) {
            objectLambdaQueryWrapper.eq(AttrEntity::getAttrId, key).or().like(AttrEntity::getAttrName, key);
        }
        IPage<AttrEntity> page = new Query<AttrEntity>().getPage(param);


        IPage<AttrEntity> page1 = this.page(page, objectLambdaQueryWrapper);

        PageUtils pageUtils = new PageUtils(page1);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespondVo> respondVo = records.stream().map(attrEntity -> {
            AttrRespondVo attrRespondVo = new AttrRespondVo();
            BeanUtils.copyProperties(attrEntity, attrRespondVo);

//            设置分组和分组名称
            LambdaQueryWrapper<AttrAttrgroupRelationEntity> attrLambdaQueryWrapper = new LambdaQueryWrapper<>();
            attrLambdaQueryWrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId());

            System.out.println("base===" + "base".equalsIgnoreCase(attrType));

            if ("base".equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(attrLambdaQueryWrapper);
                if (attrAttrgroupRelationEntity != null) {
                    Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                    if (attrGroupId != null) {
                        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                        attrRespondVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }

                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());

            if (categoryEntity != null) {
                attrRespondVo.setCateLogName(categoryEntity.getName());
            }
            return attrRespondVo;
        }).collect(Collectors.toList());

        pageUtils.setList(respondVo);
        return pageUtils;

    }

    /**
     * 根据分组id查找所有关联的基本属性
     *
     * @param attrGroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrGroupId) {
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> attrWrapper = new LambdaQueryWrapper<>();
        attrWrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupId);
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(attrWrapper);
        List<Long> collect = attrAttrgroupRelationEntities.stream().map(item -> item.getAttrId()).collect(Collectors.toList());

        List<AttrEntity> list = baseMapper.selectBatchIds(collect);

        return list;

    }

    @Override
    public void deleteRealtion(List<AttrRespondVo> vos) {
//        System.out.println("vos===" + vos);
////        vos.stream().map((item) -> {
////            LambdaQueryWrapper<AttrAttrgroupRelationEntity> attrWrapper = new LambdaQueryWrapper<>();
////            attrWrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId, item.getAttrGroupId()).
////                    eq(AttrAttrgroupRelationEntity::getAttrId, item.getAttrId());
////            attrAttrgroupRelationDao.delete(attrWrapper);
////
////            return item;
////        }).forEach(System.out::println);
////        return;
//AI优化
        vos.forEach(item -> {
            LambdaQueryWrapper<AttrAttrgroupRelationEntity> attrWrapper = new LambdaQueryWrapper<>();
            attrWrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId, item.getAttrGroupId()).
                    eq(AttrAttrgroupRelationEntity::getAttrId, item.getAttrId());
            attrAttrgroupRelationDao.delete(attrWrapper);
        });

        System.out.println("vos===" + vos);
    }


    @Override
    public AttrRespondVo getAttrInfo(Long attrId) {
        AttrRespondVo attrRespondVo = new AttrRespondVo();
//        AttrEntity byId = this.getById(attrId);
        AttrEntity attrEntity = baseMapper.selectById(attrId);
        System.out.println("AttrEntity===" + attrEntity);
        BeanUtils.copyProperties(attrEntity, attrRespondVo);


        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            LambdaQueryWrapper<AttrAttrgroupRelationEntity> attrLambdaQueryWrapper = new LambdaQueryWrapper<>();
            attrLambdaQueryWrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId());
//        设置分组信息
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(attrLambdaQueryWrapper);
            if (attrAttrgroupRelationEntity != null) {
                attrRespondVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrRespondVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }


//        设置分类信息

        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
//        System.out.println("AttrEntity===" + attrEntity);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null) {
            attrRespondVo.setCateLogName(categoryEntity.getName());
        }

        attrRespondVo.setCatelogPath(catelogPath);


        return attrRespondVo;
    }


    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        baseMapper.updateById(attrEntity);


//        x修改分组关联

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {

            LambdaUpdateWrapper<AttrAttrgroupRelationEntity> attrLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();

            attrLambdaUpdateWrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());

            Integer integer = attrAttrgroupRelationDao.selectCount(attrLambdaUpdateWrapper);
            System.out.println("integer===" + integer);
            if (integer > 0) {
                attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity, attrLambdaUpdateWrapper);
            } else {
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }


        return;
    }

}