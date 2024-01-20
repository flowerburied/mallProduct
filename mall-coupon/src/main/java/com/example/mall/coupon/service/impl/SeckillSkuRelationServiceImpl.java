package com.example.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.coupon.dao.SeckillSkuRelationDao;
import com.example.mall.coupon.entity.SeckillSkuRelationEntity;
import com.example.mall.coupon.service.SeckillSkuRelationService;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        LambdaQueryWrapper<SeckillSkuRelationEntity> skuWrapper = new LambdaQueryWrapper<>();
        String promotionSessionId = (String) params.get("promotionSessionId");

        if (!StringUtils.isEmpty(promotionSessionId)) {
            skuWrapper.eq(SeckillSkuRelationEntity::getPromotionSessionId, promotionSessionId);
        }

        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                skuWrapper
        );

        return new PageUtils(page);
    }

}