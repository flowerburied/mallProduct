package com.example.mall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.example.common.constant.AuthServerConstant;
import com.example.common.exception.BizCodeEnum;
import com.example.common.utils.R;
import com.example.mall.auth.feign.MemberFeignService;
import com.example.mall.auth.feign.ThirdPartyFeignService;
import com.example.mall.auth.vo.UserLoginVo;
import com.example.mall.auth.vo.UserRegisterVo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ResponseBody
@Controller
public class LoginServerController {

    @Resource
    ThirdPartyFeignService thirdPartyFeignService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    MemberFeignService memberFeignService;


    @PostMapping("/login")
    public String login(@RequestBody UserLoginVo userLoginVo) {

        return "redirect:http://mall.com/reg.html";

    }


    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            long oldTime = Long.parseLong(redisCode.split("_")[1]);
            int setTime = 60 * 1000;
            if (System.currentTimeMillis() - oldTime < setTime) {
//60秒内不能再发
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }
//        接口防刷
//        验证码再次校验 存key-phone value-code  sms:code:15251xx -> 1235
//        String code = UUID.randomUUID().toString().substring(0, 5);
        String code = String.valueOf(new Random().nextInt(100000));
        String subString = code + "_" + System.currentTimeMillis();
//        redis缓存验证码，防止同一个手机号在60秒内再次发送验证码
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, subString, 5, TimeUnit.MINUTES);
        thirdPartyFeignService.sendCode(phone, code);
        return R.ok();
    }

    //注册成功回到登录页
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo userRegisterVo, BindingResult result,
                           RedirectAttributes redirectAttributes,
                           HttpSession httpSession) {
        if (result.hasErrors()) {

//            result.getFieldErrors().stream().map(item->{
//                String field = item.getField();
//                String defaultMessage = item.getDefaultMessage();
//                errors.put(field,defaultMessage);
//            })
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
//            model.addAttribute("errors", errors);

            redirectAttributes.addFlashAttribute("errors", errors);
            //校验出错转发到注册页
            return "redirect:http://auth.mall.com/reg.html";
        }

//        校验验证码

        String code = userRegisterVo.getCode();
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegisterVo.getPhone());
        if (!StringUtils.isEmpty(s)) {
            String s1 = s.split("_")[0];
            if (code.equals(s1)) {
                //验证码通过
                //删除验证码,令牌机制
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegisterVo.getPhone());
                //真正注册调用远程服务注册
                R register = memberFeignService.register(userRegisterVo);
                if (register.getCode() == 0) {
                    //成功
                    return "redirect:http://auth.mall.com/login.html";

                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", register.getData(new TypeReference<String>() {
                    }));

                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.mall.com/reg.html";
                }

            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                //校验出错转发到注册页
                return "redirect:http://auth.mall.com/reg.html";
            }
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            //校验出错转发到注册页
            return "redirect:http://auth.mall.com/reg.html";
        }


    }


}
