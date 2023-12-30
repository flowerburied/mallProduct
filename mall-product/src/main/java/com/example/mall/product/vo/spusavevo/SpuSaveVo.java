package com.example.mall.product.vo.spusavevo;

import com.example.common.valid.AddGroup;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class SpuSaveVo {

    @NotEmpty(groups = {AddGroup.class})
    private String spuName;
    private String spuDescription;
    @NotEmpty(groups = {AddGroup.class})
    private Long catalogId;
    @NotEmpty(groups = {AddGroup.class})
    private Long brandId;
    private double weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    @NotEmpty(groups = {AddGroup.class})
    private List<BaseAttrs> baseAttrs;
    @NotEmpty(groups = {AddGroup.class})
    private List<Skus> skus;

}