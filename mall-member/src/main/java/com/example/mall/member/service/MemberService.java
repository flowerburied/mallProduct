package com.example.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.member.entity.MemberEntity;
import com.example.mall.member.exception.PhoneExistException;
import com.example.mall.member.exception.UserNameExistException;
import com.example.mall.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:46:43
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo memberRegisterVo);

    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUsernameUnique(String userName) throws UserNameExistException;
}

