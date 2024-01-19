package com.example.mall.member.web;

import com.example.common.utils.R;
import com.example.mall.member.feign.OrderFeignService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MemberWebController {

    @Resource
    OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum
            , Model model) {

        Map<String, Object> params = new HashMap<>();
        params.put("page", pageNum.toString());

        R r = orderFeignService.listWithItem(params);
        model.addAttribute("orders", r);


        return "orderList";
    }

}
