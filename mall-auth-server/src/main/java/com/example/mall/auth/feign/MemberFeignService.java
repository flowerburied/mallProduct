package com.example.mall.auth.feign;

import com.example.common.utils.R;
import com.example.mall.auth.vo.SocialUser;
import com.example.mall.auth.vo.UserLoginVo;
import com.example.mall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient("mall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo memberRegisterVo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo memberLoginVo);

    @PostMapping("/member/member/oauth2/login")
    R oauth2Login(@RequestBody SocialUser socialUser) throws Exception;
}
