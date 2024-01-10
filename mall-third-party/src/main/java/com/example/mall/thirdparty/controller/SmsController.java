package com.example.mall.thirdparty.controller;

import com.example.mall.thirdparty.component.Sample;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import com.example.common.utils.R;

import javax.annotation.Resource;


@RestController
@RequestMapping("/sms")
public class SmsController {

    @Resource
    Sample sample;

    @GetMapping("/smsSendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) throws Exception {
        sample.sendSmsCode(phone, code);
        return R.ok();
    }
}
