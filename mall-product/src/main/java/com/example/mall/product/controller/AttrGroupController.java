package com.example.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.mall.product.entity.AttrAttrgroupRelationEntity;
import com.example.mall.product.entity.AttrEntity;
import com.example.mall.product.service.AttrAttrgroupRelationService;
import com.example.mall.product.service.AttrService;
import com.example.mall.product.service.CategoryService;
import com.example.mall.product.vo.AttrGroupRelationVo;
import com.example.mall.product.vo.AttrGroupWithAttrVo;
import com.example.mall.product.vo.AttrRespondVo;
import com.example.mall.product.vo.skuItemvo.SpuItemAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mall.product.entity.AttrGroupEntity;
import com.example.mall.product.service.AttrGroupService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;

import javax.annotation.Resource;


/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-18 21:56:14
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Resource
    CategoryService categoryService;

    @Resource
    AttrService attrService;

    @Resource
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @GetMapping("/test/{cateLogId}/{spuId}")
    public R testVo(@PathVariable("cateLogId") Long cateLogId, @PathVariable("spuId") Long spuId) {
        List<SpuItemAttrGroupVo> attrGroupWithAttrBySpuId = attrGroupService.getAttrGroupWithAttrBySpuId(spuId, cateLogId);

        return R.ok().put("data", attrGroupWithAttrBySpuId);

    }


    //    attrgroup/165/withattr?t=1703929253540
    @GetMapping("/{cateLogId}/withattr")
    public R withAttr(@PathVariable("cateLogId") Long cateLogId) {
        List<AttrGroupWithAttrVo> data = attrGroupService.getGroupWithAttr(cateLogId);

        return R.ok().put("data", data);

    }


    //    t/attrgroup/attr/relation
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> attrGroupRelationVoList) {
        attrAttrgroupRelationService.saveBatchs(attrGroupRelationVoList);

        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody List<AttrRespondVo> vos) {
        attrService.deleteRealtion(vos);
        return R.ok();
    }

    //    noattr/relation
    @GetMapping("/{attrGroupId}/noattr/relation")
    public R noattrRelation(@PathVariable("attrGroupId") Long attrGroupId,
                            @RequestParam Map<String, Object> params) {
        PageUtils data = attrService.getNoRelationAttr(params, attrGroupId);

        return R.ok().put("data", data);
    }

    //    /attrgroup/1/attr/relation
    @GetMapping("/{attrGroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrGroupId") Long attrGroupId) {

        List<AttrEntity> list = attrService.getRelationAttr(attrGroupId);

        return R.ok().put("data", list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R listById(@RequestParam Map<String, Object> params,
                      @PathVariable("catelogId") Long catelogId) {
//        PageUtils page = attrGroupService.queryPage(params);

        PageUtils pageUtils = attrGroupService.queryPageById(params, catelogId);


        return R.ok().put("page", pageUtils);
    }

//    /**
//     * 列表
//     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params) {
//        PageUtils page = attrGroupService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);

        attrGroup.setCatelogPath(catelogPath);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
