package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.mall.product.dao.BrandDao;
import com.example.mall.product.dao.CategoryDao;
import com.example.mall.product.entity.BrandEntity;
import com.example.mall.product.entity.CategoryEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.product.dao.CategoryBrandRelationDao;
import com.example.mall.product.entity.CategoryBrandRelationEntity;
import com.example.mall.product.service.CategoryBrandRelationService;

import javax.annotation.Resource;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Resource
    BrandDao brandDao;

    @Resource
    CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        BrandEntity brandEntity = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);

        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());

        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandName(name);
        categoryBrandRelationEntity.setBrandId(brandId);
        LambdaUpdateWrapper<CategoryBrandRelationEntity> objectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        objectLambdaUpdateWrapper.eq(CategoryBrandRelationEntity::getBrandId, brandId);

        baseMapper.update(categoryBrandRelationEntity, objectLambdaUpdateWrapper);
    }

    @Override
    public void updateCatelog(CategoryEntity category) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setCatelogId(category.getCatId());
        categoryBrandRelationEntity.setCatelogName(category.getName());
        LambdaUpdateWrapper<CategoryBrandRelationEntity> objectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        objectLambdaUpdateWrapper.eq(CategoryBrandRelationEntity::getCatelogId, category.getCatId());
        baseMapper.update(categoryBrandRelationEntity, objectLambdaUpdateWrapper);
    }

    @Override
    public List<CategoryBrandRelationEntity> getBrandsList(Map<String, Object> param) {
        String key = (String) param.get("catId");

        LambdaQueryWrapper<CategoryBrandRelationEntity> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(CategoryBrandRelationEntity::getCatelogId, key);

        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = baseMapper.selectList(categoryWrapper);

        return categoryBrandRelationEntities;

    }

}