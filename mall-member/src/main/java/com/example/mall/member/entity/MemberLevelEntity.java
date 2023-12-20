package com.example.mall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:46:43
 */
@Data
@TableName("ums_member_level")
public class MemberLevelEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     *
     */
    private String name;
    /**
     *
     */
    private Integer growthPoint;
    /**
     *
     */
    private Integer defaultStatus;
    /**
     *
     */
    private BigDecimal freeFreightPoint;
    /**
     * ÿ
     */
    private Integer commentGrowthPoint;
    /**
     *
     */
    private Integer priviledgeFreeFreight;
    /**
     *
     */
    private Integer priviledgeMemberPrice;
    /**
     *
     */
    private Integer priviledgeBirthday;
    /**
     *
     */
    private String note;

}
