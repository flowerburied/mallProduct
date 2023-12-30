package com.example.mall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.example.mall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrVo {


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


    private List<AttrEntity> attrs;

}
