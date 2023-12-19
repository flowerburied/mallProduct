package com.example.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * ר
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:01:27
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

