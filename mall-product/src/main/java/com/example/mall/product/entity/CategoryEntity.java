package com.example.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:24:48
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Long catId;
    /**
     *
     */
    private String name;
    /**
     *
     */
    private Long parentCid;
    /**
     *
     */
    private Integer catLevel;
    /**
     *
     */
    @TableLogic(value = "1", delval = "0")
    private Integer showStatus;
    /**
     *
     */
    private Integer sort;
    /**
     * ͼ
     */
    private String icon;
    /**
     *
     */
    private String productUnit;
    /**
     *
     */
    private Integer productCount;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    private List<CategoryEntity> children;

}
