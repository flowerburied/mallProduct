package com.example.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.mall.product.vo.skuItemvo.SkuItemSaleAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mall.product.entity.SkuSaleAttrValueEntity;
import com.example.mall.product.service.SkuSaleAttrValueService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;

import javax.annotation.Resource;


/**
 * sku
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:56:14
 */
@RestController
@RequestMapping("product/skusaleattrvalue")
public class SkuSaleAttrValueController {
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @GetMapping("/stringList/{skuId}")
    public List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId) {
        return skuSaleAttrValueService.getSkuSaleAttrValuesAsStringList(skuId);
    }

    @GetMapping("/test/{spuId}")
    public R testVo(@PathVariable("spuId") Long spuId) {
        List<SkuItemSaleAttrsVo> saleAttrsBySpuId = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);

        return R.ok().put("data", saleAttrsBySpuId);

    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = skuSaleAttrValueService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        SkuSaleAttrValueEntity skuSaleAttrValue = skuSaleAttrValueService.getById(id);

        return R.ok().put("skuSaleAttrValue", skuSaleAttrValue);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuSaleAttrValueEntity skuSaleAttrValue) {
        skuSaleAttrValueService.save(skuSaleAttrValue);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuSaleAttrValueEntity skuSaleAttrValue) {
        skuSaleAttrValueService.updateById(skuSaleAttrValue);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        skuSaleAttrValueService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
