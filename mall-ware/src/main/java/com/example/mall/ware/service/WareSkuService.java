package com.example.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.to.mq.OrderTo;
import com.example.common.to.mq.StockLockedTo;
import com.example.common.utils.PageUtils;
import com.example.mall.ware.entity.WareSkuEntity;
import com.example.common.to.SkuHasStockVo;
import com.example.mall.ware.vo.LockStockResult;
import com.example.mall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 14:00:18
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo wareSkuLockVo);

    List<Long> listWareIdHasSkuStock(Long skuId);

    Long lockSkuStock(Long skuId, Long wareId, Integer num);


    void unlockStockTrue(Long skuId, Long wareId, Integer num, Long taskDetailId);

    void unlockStock(StockLockedTo stockLockedTo);


    void unOrderLockStock(OrderTo orderTo);
}

