package com.example.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:24:48
 */
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Long attrGroupId;
    /**
     *
     */
    private String attrGroupName;
    /**
     *
     */
    private Integer sort;
    /**
     *
     */
    private String descript;
    /**
     *
     */
    private String icon;
    /**
     *
     */
    private Long catelogId;

    @TableField(exist = false)
    private Long[] catelogPath;

}
