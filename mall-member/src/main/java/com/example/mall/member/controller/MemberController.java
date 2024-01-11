package com.example.mall.member.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.example.common.exception.BizCodeEnum;
import com.example.mall.member.exception.PhoneExistException;
import com.example.mall.member.exception.UserNameExistException;
import com.example.mall.member.feign.CouponFeignService;
import com.example.mall.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mall.member.entity.MemberEntity;
import com.example.mall.member.service.MemberService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;

import javax.annotation.Resource;


/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:46:43
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeignService couponFeignService;


    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo memberRegisterVo) {

        try {
            memberService.register(memberRegisterVo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UserNameExistException e) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }

        return R.ok();
    }

    @RequestMapping("/conpon")
    public R test() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R membercoupon = couponFeignService.membercoupon();
        return R.ok().put("member", memberEntity).put("coupon", membercoupon.get("coupon"));
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
