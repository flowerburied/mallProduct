package com.example.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.common.constant.ProductConstant;
import com.example.common.to.SkuHasStockVo;
import com.example.common.to.SkuReductionTo;
import com.example.common.to.SpuBoundTo;
import com.example.common.to.es.SkuEsModel;
import com.example.common.utils.R;
import com.example.mall.product.dao.SpuInfoDescDao;
import com.example.mall.product.entity.*;
import com.example.mall.product.feign.CouponFeignService;
import com.example.mall.product.feign.SearchFeignService;
import com.example.mall.product.feign.WareFeignService;
import com.example.mall.product.service.*;
import com.example.mall.product.vo.spusavevo.*;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.product.dao.SpuInfoDao;
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
    @Resource
    SkuImagesService skuImagesService;
    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    CouponFeignService couponFeignService;
    @Resource
    BrandService brandService;
    @Resource
    CategoryService categoryService;
    @Resource
    AttrService attrService;
    @Resource
    WareFeignService wareFeignService;

    @Resource
    SearchFeignService searchFeignService;

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
        Bounds bounds = spuInfoVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBound(spuBoundTo);

        if (r.getCode() != 0) {
            log.error("远程服务spu调用失败");
        }
//        6：保存当前spu对应的所有sku信息
//        6.1 sku的基本信息 pms_sku_info
        List<Skus> skus = spuInfoVo.getSkus();
        if (skus.size() > 0 && skus != null) {
            skus.forEach((item) -> {
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
// save base information
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();


                List<SkuImagesEntity> collect = item.getImages().stream().map((img) -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());

                    return skuImagesEntity;
                }).filter(entity -> !StringUtils.isEmpty(entity.getImgUrl())).collect(Collectors.toList());

//                sku image information  pms_sku_images

                skuImagesService.saveBatch(collect);

//                sku sales attribute information   pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> collect1 = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();

                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(collect1);
//        6.4 sku preferential Full reduction information  mall_sms->sms_ske_ladder \sku_sms_full_reduction
//                6.5 sku preferential information
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                System.out.println("item===" + item);
                System.out.println("skuReductionTo===" + skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {

                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程sku服务调用失败");
                    }
                }

            });
        }
//        skuInfoService


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        baseMapper.insert(spuInfoEntity);
    }

    @Override
    public void savespuInfoDesc(SpuInfoDescEntity spuInfoDescEntity) {
        spuInfoDescDao.insert(spuInfoDescEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        LambdaQueryWrapper<SpuInfoEntity> spuInfoWrapper = new LambdaQueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            spuInfoWrapper.and(item -> {
                item.eq(SpuInfoEntity::getId, key).or().like(SpuInfoEntity::getSpuName, key);
            });
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            spuInfoWrapper.eq(SpuInfoEntity::getPublishStatus, status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            spuInfoWrapper.eq(SpuInfoEntity::getBrandId, brandId);
        }

        String catelogId = getString(params, "catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            spuInfoWrapper.eq(SpuInfoEntity::getCatalogId, catelogId);
        }


        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                spuInfoWrapper
        );
        return new PageUtils(page);
    }

    /**
     * 商品上架
     *
     * @param spuId
     */
    @Override
    public void spuUp(Long spuId) {

//        Model required for assembly

        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attrs -> attrs.getAttrId()).collect(Collectors.toList());


        List<Long> attrEntities = attrService.selectSearchAttrs(attrIds);

        Set<Long> longs = new HashSet<>(attrEntities);


        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item -> longs.contains(item.getAttrId())).map(e -> {
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(e, attrs1);
            return attrs1;
        }).collect(Collectors.toList());


//        Find out skuId all corresponding information ,Brand name
        List<SkuInfoEntity> skus = skuInfoService.getSkuByspuId(spuId);

        List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        //            check if there is inventory available
        Map<Long, Boolean> stockMap = null; // 或者使用其他 Map 实现类，根据需求选择
//        Map<Long, Boolean> stockMaptest = null;
        try {
            R hasStock = wareFeignService.getSkuHasStock(skuIds);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
            };

            stockMap = hasStock.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getStock()));


        } catch (Exception e) {
            log.error("库存服务查询异常：{}", e);
        }

//      Encapsulate information for each Skus
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> uoProduct = skus.stream().map((item) -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(item, skuEsModel);
            skuEsModel.setSkuPrice(item.getPrice());
            skuEsModel.setSkuImg(item.getSkuDefaultImg());
            if (finalStockMap == null) {
                skuEsModel.setHasStock(true);
            } else {
                skuEsModel.setHasStock(finalStockMap.get(item));
            }
            skuEsModel.setHotScore(0L);

            BrandEntity byId = brandService.getById(item.getBrandId());
            skuEsModel.setBrandImg(byId.getLogo());
            skuEsModel.setBrandName(byId.getName());
            CategoryEntity byId1 = categoryService.getById(item.getCatalogId());
            skuEsModel.setCatalogName(byId1.getName());
            //set retrieval properties
            skuEsModel.setAttrs(attrsList);
            return skuEsModel;
        }).collect(Collectors.toList());
        //send data to ES for saving  mall-search
        R r = searchFeignService.productStatusUp(uoProduct);
        if (r.getCode() == 0) {
//            modify status
            this.upDataSpuStatus(skuIds, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
//           重复调用 接口幂等性 重试机制
        }

    }

    //    @Override
//    public void upDataSpuStatus(List<Long> skuIds, int code) {
//
//        List<SpuInfoEntity> collect = skuIds.stream().map(item -> {
//            LambdaQueryWrapper<SpuInfoEntity> spuInfoQueryWrapper = new LambdaQueryWrapper<>();
//            spuInfoQueryWrapper.eq(SpuInfoEntity::getId, item);
//            List<SpuInfoEntity> spuInfoEntities = baseMapper.selectList(spuInfoQueryWrapper);
//            return spuInfoEntities;
//        }).flatMap(List::stream)
//                .map(entity -> {
//                    entity.setUpdateTime(new Date());
//                    entity.setPublishStatus(code);
//                    return entity;
//                }).collect(Collectors.toList());
//        this.updateBatchById(collect);
//    }
    @Override
    public void upDataSpuStatus(List<Long> skuIds, int code) {
        // 一次性查询所有的SpuInfoEntity
        List<SpuInfoEntity> spuInfoEntities = baseMapper.selectBatchIds(skuIds);

        // 更新需要的字段
        spuInfoEntities.forEach(entity -> {
            entity.setUpdateTime(new Date());
            entity.setPublishStatus(code);
        });

        // 批量更新
        this.updateBatchById(spuInfoEntities);
    }


    private String getString(Map<String, Object> params, String key) {
        return (String) params.get(key);
    }


}