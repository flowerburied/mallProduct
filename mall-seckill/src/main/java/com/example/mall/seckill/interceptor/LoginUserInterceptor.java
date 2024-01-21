package com.example.mall.seckill.interceptor;


import com.example.common.constant.AuthServerConstant;
import com.example.common.vo.MemberRespondVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {


    public static ThreadLocal<MemberRespondVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        AntPathMatcher ant = new AntPathMatcher();
        String uri = request.getRequestURI();
        System.out.println("url===" + uri);
        boolean match = ant.match("/kill", uri);

        if (match) {
            // 对于RabbitMQ Listener的请求不容易验证登录（和业务不是一个线程丢失了上下文），故直接放行

            HttpSession session = request.getSession();
            MemberRespondVo attribute = (MemberRespondVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
            System.out.println("session===" + session);
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


            return true;


    }
}
