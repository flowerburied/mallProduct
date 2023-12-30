package com.example.mall.product.vo.skuItemvo;


import com.example.mall.product.vo.spusavevo.Attr;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<Attr> attrValues;
}
