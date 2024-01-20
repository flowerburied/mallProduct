package com.example.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.mall.coupon.entity.SeckillSkuRelationEntity;
import com.example.mall.coupon.service.SeckillSkuRelationService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.coupon.dao.SeckillSessionDao;
import com.example.mall.coupon.entity.SeckillSessionEntity;
import com.example.mall.coupon.service.SeckillSessionService;

import javax.annotation.Resource;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Resource
    SeckillSkuRelationService seckillSkuRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLateSession() {

        LambdaQueryWrapper<SeckillSessionEntity> seckillWrapper = new LambdaQueryWrapper<>();
        seckillWrapper.between(SeckillSessionEntity::getStartTime, startTime(), endTime());
        List<SeckillSessionEntity> list = baseMapper.selectList(seckillWrapper);

        if (CollectionUtils.isNotEmpty(list)) {
            List<SeckillSessionEntity> collect = list.stream().map(item -> {
                Long id = item.getId();
                LambdaQueryWrapper<SeckillSkuRelationEntity> relationWrapper = new LambdaQueryWrapper<>();
                relationWrapper.eq(SeckillSkuRelationEntity::getPromotionSessionId, id);
                List<SeckillSkuRelationEntity> list1 = seckillSkuRelationService.list(relationWrapper);
                item.setRelationSkus(list1);

                return item;
            }).collect(Collectors.toList());
            return collect;
        }

        return null;

    }


    private String startTime() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        LocalDateTime start = LocalDateTime.of(now, min);
        String format = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }

    private String endTime() {
        LocalDate now = LocalDate.now();
        LocalDate plus2 = now.plusDays(2);
        LocalTime max = LocalTime.MAX;
        LocalDateTime end = LocalDateTime.of(plus2, max);
        String format = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }
}