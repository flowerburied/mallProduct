package com.example.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.product.entity.SpuInfoDescEntity;
import com.example.mall.product.entity.SpuInfoEntity;
import com.example.mall.product.vo.spusavevo.SpuSaveVo;

import java.util.Map;

/**
 * spu
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:24:48
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveSpuInfo(SpuSaveVo spuInfoVo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    void savespuInfoDesc(SpuInfoDescEntity spuInfoDescEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void spuUp(Long spuId);
}

