package com.example.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.utils.R;
import com.example.mall.ware.feign.ProductFeignService;
import com.example.common.to.SkuHasStockVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.ware.dao.WareSkuDao;
import com.example.mall.ware.entity.WareSkuEntity;
import com.example.mall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        LambdaQueryWrapper<WareSkuEntity> wareSkuWrapper = new LambdaQueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            wareSkuWrapper.eq(WareSkuEntity::getSkuId, skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            wareSkuWrapper.eq(WareSkuEntity::getWareId, wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wareSkuWrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        LambdaQueryWrapper<WareSkuEntity> wareSkuWrapper = new LambdaQueryWrapper<>();
        wareSkuWrapper.eq(WareSkuEntity::getSkuId, skuId).eq(WareSkuEntity::getWareId, wareId);
        List<WareSkuEntity> wareSkuEntities = baseMapper.selectList(wareSkuWrapper);
//        判断没有这个库存，执行新增操作
        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            //add
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //远程查询sku名字  如果失败事务无需回滚
            //自己catch异常
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }

            } catch (Exception e) {

            }

            baseMapper.insert(wareSkuEntity);
        } else {
            //edit
            wareSkuEntities.forEach(item -> {
                item.setStock(item.getStock() + skuNum);
                baseMapper.updateById(item);
            });
        }
    }


    //    SELECT SUM(stock-stock_locked) FROM `wms_ware_sku` WHERE sku_id=1

    //    @Override
//    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
//        List<WareSkuEntity> wareSkuEntityList = skuIds.stream().map(skuId -> {
//            LambdaQueryWrapper<WareSkuEntity> wareWrapper = new LambdaQueryWrapper<>();
//            wareWrapper.eq(WareSkuEntity::getSkuId, skuId);
//
//            List<WareSkuEntity> wareSkuEntities = baseMapper.selectList(wareWrapper);
//
//            return wareSkuEntities;
//        }).flatMap(List::stream).collect(Collectors.toList());
//        if (wareSkuEntityList.size() == 0 || wareSkuEntityList == null) {
//            return null;
//        }
//
//        List<SkuHasStockVo> skuHasStockVoList = wareSkuEntityList.stream().map(item -> {
//            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
//            skuHasStockVo.setSkuId(item.getSkuId());
//
//            WareSkuEntity wareSkuEntity = new WareSkuEntity();
//
//            wareSkuEntity.setStock(wareSkuEntity.getStock() == null ? 0 : wareSkuEntity.getStock() + item.getStock());
//            wareSkuEntity.setStockLocked(wareSkuEntity.getStockLocked() == null ? 0 : wareSkuEntity.getStockLocked() + item.getStockLocked());
//
//            skuHasStockVo.setStock(wareSkuEntity.getStock() - wareSkuEntity.getStockLocked()  > 0);
//
//            return skuHasStockVo;
//        }).collect(Collectors.toList());
//
//        return skuHasStockVoList;
//    }
    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<WareSkuEntity> wareSkuEntityList = baseMapper.selectList(new LambdaQueryWrapper<WareSkuEntity>().in(WareSkuEntity::getSkuId, skuIds));
        if (wareSkuEntityList.isEmpty()) {
            return Collections.emptyList();
        }

        List<SkuHasStockVo> collect = wareSkuEntityList.stream().map(item -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(item.getSkuId());
            long count = item.getStock() - item.getStockLocked();
            skuHasStockVo.setStock(count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
        System.out.println("collect=====:{}" + collect);

        return collect;
    }


}