package com.example.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.common.to.mq.StockDetailTo;
import com.example.common.to.mq.StockLockedTo;
import com.example.common.utils.R;
import com.example.common.exception.NoStockException;
import com.example.mall.ware.entity.WareOrderTaskDetailEntity;
import com.example.mall.ware.entity.WareOrderTaskEntity;
import com.example.mall.ware.feign.OrderFeignService;
import com.example.mall.ware.feign.ProductFeignService;
import com.example.common.to.SkuHasStockVo;
import com.example.mall.ware.service.WareOrderTaskDetailService;
import com.example.mall.ware.service.WareOrderTaskService;
import com.example.mall.ware.vo.OrderItemVo;
import com.example.mall.ware.vo.OrderVo;
import com.example.mall.ware.vo.WareSkuLockVo;
import com.rabbitmq.client.Channel;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


import java.io.IOException;
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
    RabbitTemplate rabbitTemplate;
    @Resource
    ProductFeignService productFeignService;

    @Resource
    WareOrderTaskDetailService wareOrderTaskDetailService;
    @Resource
    WareOrderTaskService wareOrderTaskService;
    @Resource
    OrderFeignService orderFeignService;


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

    //为某个订单锁定库存  (rollbackFor =NoStockException.class )
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) {

        /**
         * 保存库存工作单的详情
         * 追溯
         * 回滚
         */
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskService.save(taskEntity);


        List<OrderItemVo> locks = wareSkuLockVo.getLocks();

        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            skuWareHasStock.setSkuId(skuId);
            skuWareHasStock.setNum(item.getCount());
            //查询这个商品在哪里有库存
            List<Long> wareId = this.listWareIdHasSkuStock(skuId);
            skuWareHasStock.setWareId(wareId);
            return skuWareHasStock;
        }).collect(Collectors.toList());


        for (SkuWareHasStock skuWareHasStock : collect) {
            Boolean skuStock = false;
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareIds = skuWareHasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                //没有仓库库存
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                Long count = this.lockSkuStock(skuId, wareId, skuWareHasStock.getNum());
//                成功1 失败0
                if (count == 1) {
/**
 * 通告锁已成功
 */
                    WareOrderTaskDetailEntity wareDetailEntity = new WareOrderTaskDetailEntity(null, skuId, "", skuWareHasStock.getNum(), taskEntity.getId(), wareId, 1);
                    wareOrderTaskDetailService.save(wareDetailEntity);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(taskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareDetailEntity, stockDetailTo);
                    //防止回滚后找不到数据
                    stockLockedTo.setDetail(stockDetailTo);

                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.lock.stock", stockLockedTo);

//                    rabbitTemplate
                    skuStock = true;
                    break;
                }
            }
            if (!skuStock) {
//                当前仓库都没锁住
                //没有仓库库存
                throw new NoStockException(skuId);

            }

        }

        return true;
    }

    @Override
    public List<Long> listWareIdHasSkuStock(Long skuId) {
        LambdaQueryWrapper<WareSkuEntity> wareWrapper = new LambdaQueryWrapper<>();
        wareWrapper.eq(WareSkuEntity::getSkuId, skuId);
        List<WareSkuEntity> wareSkuEntityList = baseMapper.selectList(wareWrapper);

        List<Long> collect = wareSkuEntityList.stream().
                filter(item -> item.getStock() - item.getStockLocked() > 0).
                map(WareSkuEntity::getWareId).
                collect(Collectors.toList());

        return collect;

    }

    @Override
    public Long lockSkuStock(Long skuId, Long wareId, Integer num) {
        LambdaQueryWrapper<WareSkuEntity> wareQueryWrapper = new LambdaQueryWrapper<>();
        wareQueryWrapper.eq(WareSkuEntity::getSkuId, skuId).eq(WareSkuEntity::getWareId, wareId);

        List<WareSkuEntity> wareSkuEntityList = baseMapper.selectList(wareQueryWrapper);

        List<WareSkuEntity> collect = wareSkuEntityList.stream().
                filter(item -> item.getStock() - item.getStockLocked() >= num)
                .peek(item -> item.setStockLocked(item.getStockLocked() + num))
                .collect(Collectors.toList());
        System.out.println("collect=====" + collect);
        if (collect.isEmpty()) {
            // 处理空列表的情况，例如抛出异常或返回特定的值
            return 0L;
        }
        boolean b = this.updateBatchById(collect);


        return b ? 1L : 0L;
    }

    @Override
    public void unlockStock(StockLockedTo stockLockedTo) {

        StockDetailTo detail = stockLockedTo.getDetail();
        Long detailId = detail.getId();
        //解锁
        WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailId);
        if (byId != null) {
            //解锁操作
            Long id = stockLockedTo.getId();  //库存工作单的ID
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();//根据订单号查询订单的状态
            R res = orderFeignService.getOrderStatus(orderSn);
            if (res.getCode() == 0) {
                OrderVo data = res.getData(new TypeReference<OrderVo>() {
                });
                System.out.println("data===" + data);
                if (data == null || data.getStatus() == 4) {
                    //订单不存在
                    //订单被取消
                    if (byId.getLockStatus() == 1) {
                        //只有当前工作单状态为已锁定但是为解锁才可以解锁
                        this.unlockStockTrue(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                }
            } else {
                throw new RuntimeException("远程服务调用失败");
            }
        }
    }

    @Override
    public void unlockStockTrue(Long skuId, Long wareId, Integer num, Long taskDetailId) {
//        wareSkuDao.unlockStock(skuId, wareId, num);
//        LambdaQueryWrapper<WareSkuEntity> wareWrapper = new LambdaQueryWrapper<>();
//        wareWrapper.eq(WareSkuEntity::getSkuId, skuId).eq(WareSkuEntity::getWareId, wareId);
//        WareSkuEntity wareSkuEntity = baseMapper.selectOne(wareWrapper);
//        wareSkuEntity.setStockLocked(wareSkuEntity.getStockLocked() - num);
//        System.out.println("wareSkuEntity==" + wareSkuEntity);
//        baseMapper.updateById(wareSkuEntity);

        LambdaUpdateWrapper<WareSkuEntity> wareSkuWrapper = new LambdaUpdateWrapper<>();
        wareSkuWrapper
                .eq(WareSkuEntity::getSkuId, skuId)
                .eq(WareSkuEntity::getWareId, wareId)
                .setSql("stock_locked = stock_locked - " + num);
        baseMapper.update(null, wareSkuWrapper);


        WareOrderTaskDetailEntity e = new WareOrderTaskDetailEntity();
        e.setId(taskDetailId);
        e.setLockStatus(2); // 1锁定 2解锁 3正常扣减
        wareOrderTaskDetailService.updateById(e);
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }


}