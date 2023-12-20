package com.example.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * skuͼƬ
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:24:49
 */
@Data
@TableName("pms_sku_images")
public class SkuImagesEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * ͼƬ
     */
    private String imgUrl;
    /**
     *
     */
    private Integer imgSort;
    /**
     * Ĭ
     */
    private Integer defaultImg;

}
