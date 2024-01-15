package com.example.mall.order.web;

import com.example.mall.order.service.OrderService;
import com.example.mall.order.vo.OrderConfirmVo;
import com.example.mall.order.vo.OrderSubmitVo;
import com.example.mall.order.vo.SubmitOrderResponseVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Resource
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }


    /**
     * 下单功能
     *
     * @param orderSubmitVo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo) {

        SubmitOrderResponseVo submitOrderResponseVo = orderService.submitOrder(orderSubmitVo);

        if (submitOrderResponseVo.getCode() == 0) {
            //下单成功到支付选择页面

            return "pay";
        } else {
            //        下单失败回到订单确认页
            return "redirect:http://order.mall.com/toTrade";

        }


    }

}
