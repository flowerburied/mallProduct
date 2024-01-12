package com.example.mall.ossclient.controller;

import com.example.common.constant.AuthServerConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {


    @Value("${sso.server.url}")
    String ssoServerUrl;


    /**
     * 无需登录访问
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }


    /**
     * 感知在ssoServer登录成功，跳转回来的
     *
     * @param model
     * @param httpSession
     * @return
     */
    @GetMapping("/boss")
    public String employees(Model model, HttpSession httpSession, @RequestParam(value = "token", required = false) String token) {

        if (!StringUtils.isEmpty(token)) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://ssoserver.com:40002/userInfo?token=" + token, String.class);
            String body = forEntity.getBody();
            httpSession.setAttribute(AuthServerConstant.LOGIN_USER, body);
        }

        Object attribute = httpSession.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null) {
//            没登录，跳转到登录服务器

            return "redirect:" + ssoServerUrl + "?redirect_url=http://client2.com:40003/boss";
        } else {
            List<String> emps = new ArrayList<>();
            emps.add("张三");
            emps.add("李四");

            model.addAttribute("emps", emps);
            return "list";
        }


    }

}
