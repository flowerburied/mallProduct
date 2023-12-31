package com.example.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.ware.dao.WareInfoDao;
import com.example.mall.ware.entity.WareInfoEntity;
import com.example.mall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        LambdaQueryWrapper<WareInfoEntity> wareInfoWrapper = new LambdaQueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wareInfoWrapper.and(item -> {
                item.eq(WareInfoEntity::getId, key).or()
                        .like(WareInfoEntity::getName, key).or()
                        .like(WareInfoEntity::getAddress, key).or()
                        .like(WareInfoEntity::getAreacode, key);
            });
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wareInfoWrapper
        );

        return new PageUtils(page);
    }

}