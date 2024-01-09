package com.example.mall.product.web;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.example.mall.product.service.SkuInfoService;
import com.example.mall.product.vo.skuItemvo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException {
        SkuItemVo vo = skuInfoService.item(skuId);
//        model.addAttribute("item",vo);
        return "item";
    }
}
