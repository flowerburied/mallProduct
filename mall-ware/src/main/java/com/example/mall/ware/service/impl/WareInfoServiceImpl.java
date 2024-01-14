package com.example.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.utils.R;
import com.example.mall.ware.feign.MemberFeignService;
import com.example.mall.ware.vo.FareVo;
import com.example.mall.ware.vo.MemberAddressVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.ware.dao.WareInfoDao;
import com.example.mall.ware.entity.WareInfoEntity;
import com.example.mall.ware.service.WareInfoService;

import javax.annotation.Resource;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {


    @Resource
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        LambdaQueryWrapper<WareInfoEntity> wareInfoWrapper = new LambdaQueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wareInfoWrapper.and(item -> {
                item.eq(WareInfoEntity::getId, key).or()
                        .like(WareInfoEntity::getName, key).or()
                        .like(WareInfoEntity::getAddress, key).or()
                        .like(WareInfoEntity::getAreacode, key);
            });
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wareInfoWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据用户收货地址计算运费
     *
     * @param addrId
     * @return
     */
    @Override
    public FareVo getFare(Long addrId) {

        FareVo fareVo = new FareVo();
        R info = memberFeignService.addrInfo(addrId);
        MemberAddressVo data = info.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {
        });
        fareVo.setAddressVo(data);
        if (data != null) {
            String phone = data.getPhone();
            String substring = phone.substring(phone.length() - 1, phone.length());
            fareVo.setFare(new BigDecimal(substring));

            return fareVo;
        }
        fareVo.setFare(new BigDecimal(0));
        return fareVo;

    }

}