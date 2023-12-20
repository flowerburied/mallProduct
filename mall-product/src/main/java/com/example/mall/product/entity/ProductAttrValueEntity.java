package com.example.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * spu
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:24:49
 */
@Data
@TableName("pms_product_attr_value")
public class ProductAttrValueEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     *
     */
    private Long spuId;
    /**
     *
     */
    private Long attrId;
    /**
     *
     */
    private String attrName;
    /**
     *
     */
    private String attrValue;
    /**
     * ˳
     */
    private Integer attrSort;
    /**
     *
     */
    private Integer quickShow;

}
