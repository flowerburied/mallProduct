package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.product.dao.CategoryBrandRelationDao;
import com.example.mall.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.product.dao.BrandDao;
import com.example.mall.product.entity.BrandEntity;
import com.example.mall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Resource
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> objectQueryWrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(key)) {
            objectQueryWrapper.eq("brand_id", key).or().like("name", key);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                objectQueryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void updateDetail(BrandEntity brand) {
//        保证冗余字段一致
        baseMapper.updateById(brand);

        if (!StringUtils.isEmpty(brand.getName())) {
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
        }


    }

    @Override
    public List<BrandEntity> getBrands(List<Long> brandId) {

        LambdaQueryWrapper<BrandEntity> brandWrapper = new LambdaQueryWrapper<>();
        brandWrapper.in(BrandEntity::getBrandId, brandId);

        List<BrandEntity> list = baseMapper.selectList(brandWrapper);
        return list;
    }

}