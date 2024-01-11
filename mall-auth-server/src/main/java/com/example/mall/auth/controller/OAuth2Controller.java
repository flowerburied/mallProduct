package com.example.mall.auth.controller;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.HttpUtils;
import com.example.common.utils.R;
import com.example.mall.auth.feign.MemberFeignService;
import com.example.mall.auth.vo.GitEEUser;
import com.example.mall.auth.vo.MemberRespondVo;
import com.example.mall.auth.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录
 */
@Controller
public class OAuth2Controller {

    @Resource
    MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/gitee/success")
    public String gitEEOAth(@RequestParam("code") String code, HttpSession session, HttpServletResponse httpServletResponse) throws Exception {
        //根据code换取access_token
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("grant_type", "authorization_code");
        map.put("client_id", "2573be535a0e1c38eb823d99b84449a764ba59ba3139c9887d1e363ba2dcf2e2");
        map.put("redirect_uri", "http://auth.mall.com/oauth2.0/gitee/success");
        map.put("client_secret", "e4944df4c04321f23a7ad89a8f99084e9394d17ff744289dc4eb68cb3c735a04");
        System.out.println("code===" + code);
        HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", new HashMap<>(), map, new HashMap<>());
//https://gitee.com/oauth/token
        System.out.println("response.getStatusLine()==" + response.getStatusLine().getStatusCode());
////        处理
        if (response.getStatusLine().getStatusCode() == 200) {
//            码云认证服务器返回 access_token
            String json = EntityUtils.toString(response.getEntity());
//            System.out.println("response.doPost==" + json);
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            Long oAuthId = getOAuthId(socialUser.getAccess_token());
            socialUser.setUid(oAuthId);
            System.out.println("socialUser===" + socialUser);
//            知道当前是哪个社交用户
//            如果是第一次,则自动注册

            R oauth2Login = memberFeignService.oauth2Login(socialUser);
            if (oauth2Login.getCode() == 0) {

                MemberRespondVo data = oauth2Login.getData("data", new TypeReference<MemberRespondVo>() {
                });
            //第一次使用session，命令游览器保存卡号，JSESSIONID这个cookie
                //以后浏览器访问哪个网站就会带上这个网站的cookie
                session.setAttribute("loginUser", data);
//                httpServletResponse.addCookie(new Cookie());
                //登录成功就跳回首页
                return "redirect:http://mall.com";
            } else {
                return "redirect:http://auth.mall.com/login.html";
            }
        } else {
            return "redirect:http://auth.mall.com/login.html";
        }

    }

    private Long getOAuthId(String accessToken) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("access_token", accessToken);

        HttpResponse response = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<>(), map);
        String json = EntityUtils.toString(response.getEntity());
        GitEEUser gitEEUser = JSON.parseObject(json, GitEEUser.class);

        return gitEEUser.getId();
    }
}
