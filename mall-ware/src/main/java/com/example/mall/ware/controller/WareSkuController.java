package com.example.mall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.common.exception.BizCodeEnum;
import com.example.common.to.SkuHasStockVo;
import com.example.common.exception.NoStockException;
import com.example.mall.ware.vo.FareVo;
import com.example.mall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mall.ware.entity.WareSkuEntity;
import com.example.mall.ware.service.WareSkuService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;


/**
 * 商品库存
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 14:00:18
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;


    @GetMapping("/testSql")
    public R testFun(@RequestParam("skuId") Long skuId, @RequestParam("wareId") Long wareId, @RequestParam("num") Integer num, @RequestParam("taskDetailId") Long taskDetailId) {

        wareSkuService.unlockStockTrue(skuId, wareId, num, taskDetailId);
        return R.ok();
    }

    @GetMapping("/sql")
    public R testSql(@RequestParam("skuId") Long skuId, @RequestParam("wareId") Long wareId, @RequestParam("num") Integer num) {
        Long aLong = wareSkuService.lockSkuStock(skuId, wareId, num);
        return R.ok().setData(aLong);
    }

    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo) {

        try {
            Boolean lockStockResults = wareSkuService.orderLockStock(wareSkuLockVo);
            return R.ok();
        } catch (NoStockException err) {
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), BizCodeEnum.NO_STOCK_EXCEPTION.getMsg());
        }


    }

    @PostMapping("/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds) {

        List<SkuHasStockVo> skuHasStockVoList = wareSkuService.getSkuHasStock(skuIds);

        return R.ok().setData(skuHasStockVoList);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
