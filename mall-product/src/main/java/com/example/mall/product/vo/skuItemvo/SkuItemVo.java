package com.example.mall.product.vo.skuItemvo;


import com.example.mall.product.entity.SkuImagesEntity;
import com.example.mall.product.entity.SkuInfoEntity;
import com.example.mall.product.entity.SpuInfoDescEntity;
import com.example.mall.product.vo.spusavevo.SeckillInfoVo;
import lombok.Data;


import java.util.List;

@Data
public class SkuItemVo {
    //1、sku基本信息的获取  pms_sku_info
    private SkuInfoEntity info;

    private boolean hasStock = true;

    //2、sku的图片信息    pms_sku_images
    private List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合
    private List<SkuItemSaleAttrsVo> saleAttr;

    //4、获取spu的介绍
    private SpuInfoDescEntity desc;

    //5、获取spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;

    //6、秒杀商品的优惠信息
    private SeckillInfoVo seckillSkuVo;

}
