package com.example.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.member.dao.MemberLevelDao;
import com.example.mall.member.entity.MemberLevelEntity;
import com.example.mall.member.exception.PhoneExistException;
import com.example.mall.member.exception.UserNameExistException;
import com.example.mall.member.service.MemberLevelService;
import com.example.mall.member.vo.MemberRegisterVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.member.dao.MemberDao;
import com.example.mall.member.entity.MemberEntity;
import com.example.mall.member.service.MemberService;

import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo memberRegisterVo) {

        MemberEntity memberEntity = new MemberEntity();

//设置默认等级
        MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());
//        检查用户名和手机号是否唯一,使用异常机制，让controller感知异常
        this.checkPhoneUnique(memberRegisterVo.getPhone());
        this.checkUsernameUnique(memberRegisterVo.getUserName());
        memberEntity.setMobile(memberRegisterVo.getPhone());
        memberEntity.setUsername(memberRegisterVo.getUserName());

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(memberRegisterVo.getPassword());
        memberEntity.setPassword(encode);

        //其他的默认信息

        baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        LambdaQueryWrapper<MemberEntity> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(MemberEntity::getMobile, phone);
        Integer integer = baseMapper.selectCount(memberWrapper);
        if (integer > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUsernameUnique(String userName) throws UserNameExistException {
        LambdaQueryWrapper<MemberEntity> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(MemberEntity::getUsername, userName);
        Integer integer = baseMapper.selectCount(memberWrapper);
        if (integer > 0) {
            throw new UserNameExistException();
        }
    }

}