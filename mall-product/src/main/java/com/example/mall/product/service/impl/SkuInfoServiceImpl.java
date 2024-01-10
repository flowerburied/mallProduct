package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.product.config.ThreadPoolConfigProperties;
import com.example.mall.product.entity.SkuImagesEntity;
import com.example.mall.product.entity.SpuInfoDescEntity;
import com.example.mall.product.entity.SpuInfoEntity;
import com.example.mall.product.service.*;
import com.example.mall.product.vo.skuItemvo.SkuItemSaleAttrsVo;
import com.example.mall.product.vo.skuItemvo.SkuItemVo;
import com.example.mall.product.vo.skuItemvo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.product.dao.SkuInfoDao;
import com.example.mall.product.entity.SkuInfoEntity;

import javax.annotation.Resource;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Resource
    SkuImagesService skuImagesService;
    @Resource
    SpuInfoDescService spuInfoDescService;
    @Resource
    AttrGroupService attrGroupService;
    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    ThreadPoolExecutor threadPoolExecutor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        LambdaQueryWrapper<SkuInfoEntity> skuInfoWrapper = new LambdaQueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            skuInfoWrapper.and(item -> {
                item.eq(SkuInfoEntity::getSkuId, key).or().like(SkuInfoEntity::getSkuName, key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            skuInfoWrapper.eq(SkuInfoEntity::getCatalogId, catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            skuInfoWrapper.eq(SkuInfoEntity::getBrandId, brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            skuInfoWrapper.ge(SkuInfoEntity::getPrice, min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max)) {

            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
                    skuInfoWrapper.le(SkuInfoEntity::getPrice, max);
                }
            } catch (Exception e) {

            }

        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                skuInfoWrapper
        );

        return new PageUtils(page);


    }

    @Override
    public List<SkuInfoEntity> getSkuByspuId(Long spuId) {

        LambdaQueryWrapper<SkuInfoEntity> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.eq(SkuInfoEntity::getSpuId, spuId);
        List<SkuInfoEntity> skuInfoEntities = baseMapper.selectList(skuWrapper);
        return skuInfoEntities;


    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //sku基本信息
            SkuInfoEntity skuInfoEntity = baseMapper.selectById(skuId);
//                    Long spuId = skuInfoEntity.getSpuId();
//            Long catalogId = skuInfoEntity.getCatalogId();
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, threadPoolExecutor);

        CompletableFuture<Void> saleFuture = infoFuture.thenAcceptAsync((res) -> {
//spu销售组合
            List<SkuItemSaleAttrsVo> skuItemSaleAttrsVoList = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(skuItemSaleAttrsVoList);
        }, threadPoolExecutor);
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //        获取spu介绍
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesp(spuInfoDescEntity);
        }, threadPoolExecutor);
        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //        获取spu规格参数信息
            List<SpuItemAttrGroupVo> SpuItemAttrGroupEntity = attrGroupService.getAttrGroupWithAttrBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(SpuItemAttrGroupEntity);

        }, threadPoolExecutor);

        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            //        sku图片信息
            List<SkuImagesEntity> skuImagesEntity = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setImagesEntites(skuImagesEntity);
        }, threadPoolExecutor);

        //等待所有任务都完成
        CompletableFuture.allOf(saleFuture, descFuture, baseAttrFuture, imagesFuture).get();


        return skuItemVo;

    }


}