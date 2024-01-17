package com.example.mall.order.web;

import com.example.mall.order.entity.OrderEntity;
import com.example.mall.order.service.OrderService;
import com.example.mall.order.vo.OrderConfirmVo;
import com.example.mall.order.vo.OrderSubmitVo;
import com.example.mall.order.vo.SubmitOrderResponseVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Resource
    OrderService orderService;
    @Resource
    RabbitTemplate rabbitTemplate;


    @ResponseBody
    @GetMapping("/test/createOrder")
    public String createOrderTest() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn("111111");
        orderEntity.setModifyTime(new Date());
        rabbitTemplate.convertAndSend("order-event-exchange",
                "order-create-order",
                orderEntity);

        return "ok";
    }


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
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes) {

        SubmitOrderResponseVo submitOrderResponseVo = orderService.submitOrder(orderSubmitVo);

        if (submitOrderResponseVo.getCode() == 0) {
            //下单成功到支付选择页面
            model.addAttribute("submitOrderResp", submitOrderResponseVo);
            return "pay";
        } else {
            String msg = "下单失败：";
            switch (submitOrderResponseVo.getCode()) {
                case 1:
                    msg += "订单信息过期，请重新提交";
                    break;
                case 2:
                    msg += "商品价格异常，请重新提交";
                    break;
                case 3:
                    msg += "商品库存不足，请重新提交";
                    break;
            }

            redirectAttributes.addFlashAttribute("msg", msg);
            //        下单失败回到订单确认页
            return "redirect:http://order.mall.com/toTrade";

        }


    }

}
