package com.example.mall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mall.order.entity.OrderEntity;
import com.example.mall.order.service.OrderService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;


/**
 * ����
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:57:24
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;


    @GetMapping("/testFun")
    public R testFun(@RequestParam("orderSn") String orderSn, @RequestParam("id") String id) {
        System.out.println("orderSn===" + orderSn);
        orderService.testFun(orderSn, id);
        return R.ok();
    }


    @GetMapping("/status/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn) {
        OrderEntity orderEntity = orderService.getOrderByOrderSn(orderSn);

        return R.ok().setData(orderEntity);
    }

    @PostMapping("/listWithItem")
    public R listWithItem(@RequestBody Map<String, Object> params) {
        PageUtils page = orderService.queryPageWithItem(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order) {
        orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order) {
        orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
