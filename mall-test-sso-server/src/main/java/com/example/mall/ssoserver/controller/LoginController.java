package com.example.mall.ssoserver.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class LoginController {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @ResponseBody
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("token") String token) {
        String s = stringRedisTemplate.opsForValue().get(token);
        return s;
    }

    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String redirectUrl,
                            Model model,
                            @CookieValue(value = "sso_token", required = false) String ssoToken) {

        if (!StringUtils.isEmpty(ssoToken)) {
            //说明已经在线 vue逻辑：子到父，如果父已经登录直接返回（都差不多）
            //登录成功
            return "redirect:" + redirectUrl + "?token=" + ssoToken;
        }
        System.out.println("redirectUrl===" + redirectUrl);
        model.addAttribute("url", redirectUrl);
        return "login";
    }


    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("url") String url,
                          HttpServletResponse httpServletResponse) {

        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {

            String replace = UUID.randomUUID().toString().replace("_", "");
            stringRedisTemplate.opsForValue().set(replace, username);
            Cookie cookie = new Cookie("sso_token", replace);
            httpServletResponse.addCookie(cookie);
            //登录成功
            return "redirect:" + url + "?token=" + replace;
        }

        //登录失败跳转
        return "login";
    }
}
