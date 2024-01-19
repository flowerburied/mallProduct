package com.example.mall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;

import com.example.mall.order.vo.pay.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "9021000133601905";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCXktR9Q3j2M3l2z5T7Dv/tOdaH2g8jfNVRwnMoa0v0O++v1wdLmxRmtYhY4/Yp2DBvWc3Sp3KPyiu6RBrof49DoxWW1KtGIX7Jmx3tGW0ReV6h9OCui5vaVqPJ2yuLmXl1Al+URq7JTdWxEHfZUKCkudbt6aN1xH1bqQvmgIwzbybL+ds+fTBUSiBXhxkJegDO+EdyzkyNgNiMlUOIYpVqYSoYkJ2fwCHzD9IqIJJyQxf3U4eBB9O2fHvOnTNRSgmHuGCD4XMaA/JvMhKGI9/DbfxypHXfppoIhr0GkV6+S3ltc8hjKsSubhmRS0nvMSc4VLzWea+PVAhfirObG9u/AgMBAAECggEBAI6xQut+FqUoxmOMfoNvlo280nLoXTIadjRXK9RUVpfhyP6wHwUVRLqAkZClYvv3s3lQgMycqOQm/s2HChHw7G1PXaa2JVuLcvHW3vBz7FL0AJvVjaOkeJ+uytjgRPlSDZ6TEmnjtO58IP1lNH7YQbNx5J3rQuA+j4k39qUURWiw+2AdI9GX/qHJOr5Ww2LX0EzJQOhh/6D8hhrKyMNXUXdESU/hSDdWYgzOfsLtqHvhkl5Ing+7crMCFLOXXHaOr/9K4R26AfYh+KCwrEtWIrDSqgiTb64WIfrGTmX/fnh7gvzS4N8oUMh8jInM0Y/ltTT3opbqrUhys15sI8wjS7ECgYEAxUFC3BnQhh9OI1PUFb9cgYw0o6kLwwf7oN3z1/nuMv41B58/EQEGVqeoZatetBsHOZbhNcx31Sh1ICAy7qDMFCW01cDVJLC2v5cW+3cMZbw1sx9Xo2A7sFQpRAJGPhicHIY/EqerjiZrRkw0tTrt9QSBdU4HN3TzuXGFyptqqkkCgYEAxLbQ0n5e8oWlwmLwwGd907A/8SQdnVBXmYA2cci4Ci5OpW8Jymzyuaag46+8+HqrMqEMF1b0wSFzVBsktHShGdOLKznFkwUEZEOL/A0Rt6IYdjldbT58YIhrKRurzwSWjm6cY9VK23V6lSL7yYrq6jnHvOXp1N9vcQ2dM41tlccCgYALduSM6E8vTGh9MyUTcoDM4ro4fN27Ix6j1eKTPepzOkUYwU+oDFgy9yGZbyFc9GkyFqvWNaENWYGdMWHkbqIN1bcx6Tg6Hq8waXajfOLSkuLIG107mJ8/e1S+qCcu+r0wBzlpOyeEZoU7m7hAhEFKSU3A5J0Uww5bS9bL1MaZSQKBgFwV1IVlAN947tKjbF41VDK/g/9HT9GvpYJHAbWcJ4MkkE5jP2sSO0HrHc/YGZccHX4y460dWK/8D7fkiPcszamAXNqcTb/4Dkk9UY9pMs0KxXa6feDA6opp8fgpavEbDbuZTf5x/3vib8LWwJVlQXjqGXV1D1RGUC3VGKzkXNNLAoGBAL0ned7u0+je3Gfg/nbXBh2uE1oJgPjYZImvs/arhdxZ/LP9pr44bYCFhqmbHbcDyJVjCXhiKQSkrvxLZpOUxxuJHWOoIbng6EBEen3tQ14dzU3dkIhUxzdjbFDGA+IUByzP0mjg+kvrt9FPwGTO6jhPoxbUzfjed3bP/5hHTRAG";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAipeo9RJ3UaQYo7I5KPZNZA2YEqVc1CUrp9kvHMaG8P1W8eK5L7QjHp5YRs6rHlXChG9BCZmmVC0bEZG0rgtVA/FGN+SLZ9/W2772xYNZzFEN6B3zCBMO0JH8gIdiFowl4xVxvGDMesx2FuIUK6ooNC3axxKPHOTMNR2nb9Uk14TXp5nVUr3cKoe5PYW0gTAntTee4izGJ/fAIcm1XYvT7j36eeHsYvoFoV1OBgViXcdSzEFLQq4obzVUKpwF99AN6OV3K4yuNYgWU76w4kXK+mxndCXxpRCSHl4h/oDLcLEATNMswmTmGlfXn1F3Rfg66qa67852pfgLJdtfPqVSWwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url = "http://member.mall.com/memberOrder.html";

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    private String timeout = "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
//    https://openapi-sandbox.dl.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }
}
