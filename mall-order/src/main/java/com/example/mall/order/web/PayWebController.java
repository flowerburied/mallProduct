package com.example.mall.order.web;

import com.alipay.api.AlipayApiException;
import com.example.mall.order.config.AlipayTemplate;
import com.example.mall.order.service.OrderService;
import com.example.mall.order.vo.pay.PayVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class PayWebController {

    @Resource
    AlipayTemplate alipayTemplate;
    @Resource
    OrderService orderService;

    @ResponseBody
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {


        PayVo payVo = orderService.getOrderPay(orderSn);


//        PayVo payVo = new PayVo();
        String pay = alipayTemplate.pay(payVo);

        System.out.println("pay===" + pay);
        return pay;
    }

}
