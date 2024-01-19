package com.example.mall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.mall.order.config.AlipayTemplate;
import com.example.mall.order.service.OrderService;
import com.example.mall.order.vo.pay.PayAsyncVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class OrderPayListener {

    @Resource
    OrderService orderService;
    @Resource
    AlipayTemplate alipayTemplate;

    @PostMapping("/payed/notify")
    public String handleAliPayed(PayAsyncVo payAsyncVo, HttpServletRequest request) throws AlipayApiException {
//        http://member.mall.com/memberOrder.html?charset=utf-8&out_trade_no=202401191713566921748272570070683650&method=alipay.trade.page.pay.return&total_amount=10001.00&sign=HbzlBPIiYawvSz03MfNfaE51YjSCiUqeumrgy7gdbWxqSb1K9RhO4FyHHS5GqqPPUA9RC6AjHNGBanCLmF2By46DZpV%2BOksngzsDadjvxM%2BrngK6h9HSApZ47fzqkX%2BEaEeW7nWtFq1UMh7lHbsHKYpgB85psWMyCCEDzZOx%2BR5HKJtZLKeIDXF1YPxxZy5OOk2leghuLae9Fd9fCRgtOWXnJGc%2FmgRGf9RLWt8CTR6Nm3xbXuMcqOIqPM9TL%2B9DIdhSwo3y9aT12j9PI66ctiKQrG%2Fy0FxEpmShGplM4qiO%2BRiA7e6c3j7nnuov6upR9bSgDSbO1pOy5NiXQUk7BQ%3D%3D&trade_no=2024011922001434610501880218&auth_app_id=9021000133601905&version=1.0&app_id=9021000133601905&sign_type=RSA2&seller_id=2088721024967570&timestamp=2024-01-19+17%3A14%3A46
        //收到支付宝的异步通知，通知订单成功
//        Map<String, String[]> map = request.getParameterMap();
//        for (String key : map.keySet()) {
//            String parameter = request.getParameter(key);
//
//            System.out.println("parameter===" + parameter);
//        }
//        System.out.println("支付宝通知===" + map);
//        String result = orderService.handlePayResult(payAsyncVo);

        // 1.验签
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        boolean sign = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type());
        System.out.println("支付宝签名认证状态===" + sign);
        // 2.改本地数据库
        if (sign) {
            orderService.handlePayResult(payAsyncVo);
            return "success";
        }


        return "error";
    }
}
