package com.example.mall.product.entity;

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
@TableName("pms_spu_comment")
public class SpuCommentEntity implements Serializable {
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
     * spu_id
     */
    private Long spuId;
    /**
     *
     */
    private String spuName;
    /**
     *
     */
    private String memberNickName;
    /**
     *
     */
    private Integer star;
    /**
     *
     */
    private String memberIp;
    /**
     *
     */
    private Date createTime;
    /**
     *
     */
    private Integer showStatus;
    /**
     *
     */
    private String spuAttributes;
    /**
     *
     */
    private Integer likesCount;
    /**
     *
     */
    private Integer replyCount;
    /**
     *
     */
    private String resources;
    /**
     *
     */
    private String content;
    /**
     *
     */
    private String memberIcon;
    /**
     *
     */
    private Integer commentType;

}
