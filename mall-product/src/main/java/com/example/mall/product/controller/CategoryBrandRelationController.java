package com.example.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mall.product.entity.CategoryBrandRelationEntity;
import com.example.mall.product.service.CategoryBrandRelationService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;


/**
 * Ʒ
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:56:14
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    /**
     * 列表
     */
//    @RequestMapping(value = "/catelog/list",method = RequestMethod.GET)
    @GetMapping("/catelog/list")
    public R cateloglist(@RequestParam("brandId") Long brandId) {
        if (brandId == null) {
            return R.error("品牌ID不能为空");
        }

//        QueryWrapper<CategoryBrandRelationEntity> objectQueryWrapper = new QueryWrapper<>();
//        objectQueryWrapper.eq("brand_id", brandId);
//        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(objectQueryWrapper);

        LambdaQueryWrapper<CategoryBrandRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryBrandRelationEntity::getBrandId, brandId);
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(queryWrapper);


        return R.ok().put("data", list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
