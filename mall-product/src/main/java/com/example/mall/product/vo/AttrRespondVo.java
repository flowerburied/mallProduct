package com.example.mall.product.vo;

import lombok.Data;

@Data
public class AttrRespondVo extends AttrVo {

    /**
     * 分类名称
     * 分组名称
     */
    private String cateLogName;

    private String groupName;

    private Long[] catelogPath;

}
