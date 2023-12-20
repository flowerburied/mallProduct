package com.example.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mall.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:46:43
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

