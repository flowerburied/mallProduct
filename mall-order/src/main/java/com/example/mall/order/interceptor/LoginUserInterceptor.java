package com.example.mall.order.interceptor;


import com.example.common.constant.AuthServerConstant;
import com.example.common.vo.MemberRespondVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {


    public static ThreadLocal<MemberRespondVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        MemberRespondVo attribute = (MemberRespondVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null) {
            loginUser.set(attribute);
            return true;
        } else {
            //没登录就去登录
            request.getSession().setAttribute("msg", "请先登录");
            response.sendRedirect("http://auth.mall.com/login.html");
            return false;
        }


    }
}
