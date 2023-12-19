package com.example.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * ֧����Ϣ��
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:57:25
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

