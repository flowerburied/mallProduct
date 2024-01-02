package com.example.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.mall.product.entity.ProductAttrValueEntity;
import com.example.mall.product.service.ProductAttrValueService;
import com.example.mall.product.vo.AttrRespondVo;
import com.example.mall.product.vo.AttrVo;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mall.product.entity.AttrEntity;
import com.example.mall.product.service.AttrService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;

import javax.annotation.Resource;


/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:56:14
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Resource
    ProductAttrValueService productAttrValueService;

    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(
            @PathVariable("spuId") Long spuId) {
      List<ProductAttrValueEntity> entities= productAttrValueService.baseAttrListForSpu(spuId);

        return R.ok().put("data", null);
    }


    @GetMapping("/{attrType}/list/{catId}")
    public R baseList(@RequestParam Map<String, Object> param,
                      @PathVariable("catId") Long catId,
                      @PathVariable("attrType") String attrType) {

        PageUtils pageUtils = attrService.queryPageById(param, catId, attrType);

        return R.ok().put("page", pageUtils);


    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {
//        AttrEntity attr = attrService.getById(attrId);
        AttrRespondVo attr = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr) {
        attrService.updateAttr(attr);

        return R.ok();
    }

    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(
            @PathVariable Long spuId,
            @RequestBody List<ProductAttrValueEntity> productAttrValueEntities){

        productAttrValueService.updateSpuAttr(spuId,productAttrValueEntities);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
