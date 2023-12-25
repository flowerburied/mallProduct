package com.example.mall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.common.valid.AddGroup;
import com.example.common.valid.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.product.entity.BrandEntity;
import com.example.mall.product.service.BrandService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;

import javax.validation.Valid;


/**
 * Ʒ
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:56:14
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand /**, BindingResult bindingResult**/) {

//        if (bindingResult.hasErrors()) {
////            获取校验的结果
//            Map<String, String> map = new HashMap<>();
//            bindingResult.getFieldErrors().forEach((item) -> {
//                String defaultMessage = item.getDefaultMessage();
//                String field = item.getField();
//                map.put(field, defaultMessage);
//
//            });
//
//            return R.error(400, "数据不合法").put("data", map);
//        } else {
//            brandService.save(brand);
//        }

        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand) {
        brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
