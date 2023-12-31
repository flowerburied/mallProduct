package com.example.mall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.common.to.SkuHasStockVo;
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


    @PostMapping("/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds) {

        List<SkuHasStockVo> skuHasStockVoList = wareSkuService.getSkuHasStock(skuIds);
        R ok = R.ok();
        ok.setData(skuHasStockVoList);
        return ok;
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
