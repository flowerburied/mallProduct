package com.example.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Ʒ
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:24:48
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Ʒ
     */
    @TableId
    private Long brandId;
    /**
     * Ʒ
     */
    @NotBlank(message = "品牌名不能为空")
    private String name;
    /**
     * Ʒ
     */
    @NotEmpty
    @URL(message = "必须是合法的url地址")
    private String logo;
    /**
     *
     */
    private String descript;
    /**
     *
     */
    private Integer showStatus;
    /**
     *
     */
    @NotEmpty
    @Pattern(regexp = "/^[a-zA-Z]$/", message = "检索首字母必须是一个字母")
    private String firstLetter;
    /**
     *
     */
    @NotNull
    @Min(value = 0, message = "排序必须大于等于零")
    private Integer sort;

}
