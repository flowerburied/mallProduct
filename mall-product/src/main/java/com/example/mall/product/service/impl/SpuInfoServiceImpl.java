package com.example.mall.product.service.impl;

import com.example.mall.product.dao.SpuInfoDescDao;
import com.example.mall.product.entity.SpuImagesEntity;
import com.example.mall.product.entity.SpuInfoDescEntity;
import com.example.mall.product.service.ProductAttrValueService;
import com.example.mall.product.service.SkuInfoService;
import com.example.mall.product.service.SpuImagesService;
import com.example.mall.product.vo.spusavevo.BaseAttrs;
import com.example.mall.product.vo.spusavevo.Skus;
import com.example.mall.product.vo.spusavevo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.product.dao.SpuInfoDao;
import com.example.mall.product.entity.SpuInfoEntity;
import com.example.mall.product.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Resource
    SpuInfoDescDao spuInfoDescDao;
    @Resource
    SpuImagesService spuImagesService;
    @Resource
    ProductAttrValueService productAttrValueService;
    @Resource
    SkuInfoService skuInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfoVo) {

//        保存spu基本信息  pms_spu_info

        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
//        保存spu描述图片 pms_spu_info_desc
        List<String> decript = spuInfoVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        this.savespuInfoDesc(spuInfoDescEntity);
//        保存spu图片集 pms_spu_images
//        SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
        List<String> images = spuInfoVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);
//        保存spu规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfoVo.getBaseAttrs();

        productAttrValueService.saveProductAttr(baseAttrs, spuInfoEntity.getId());
//        保存spu积分信息 mall_sms -> sms_spu_bounds

//        6：保存当前spu对应的所有sku信息
//        6.1 sku的基本信息 pms_sku_info
        List<Skus> skus = spuInfoVo.getSkus();
        if(skus.size()>0&& skus!=null){

        }
//        skuInfoService
//        6.2 sku的图片信息 pms_sku_images
//        6.3 sku的sale attribute information  pms_sku_sale_attr_value
//        6.4 sku的preferential Full reduction information  mall_sms->sms_ske_ladder \sku_sms_full_reduction
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        baseMapper.insert(spuInfoEntity);
    }

    @Override
    public void savespuInfoDesc(SpuInfoDescEntity spuInfoDescEntity) {
        spuInfoDescDao.insert(spuInfoDescEntity);
    }


}