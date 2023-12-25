package com.example.mall.product.service.impl;

import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.product.dao.CategoryDao;
import com.example.mall.product.entity.CategoryEntity;
import com.example.mall.product.service.CategoryService;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    @Resource
//    CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
//        Identify all categories
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
//        Convert to a father son tree structure
        List<CategoryEntity> collect = categoryEntities.stream().filter((menus) -> {
//            System.out.println(menus.getParentCid() == 0);
            return menus.getParentCid() == 0;
        }).map((item) -> {
//            System.out.println(item);
            item.setChildren(setChildren(item, categoryEntities));
            return item;
        }).sorted((item1, item2) -> {
            return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getSort() == null ? 0 : item2.getSort());
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
//        TODO 检查当前删除的菜单，是否被别的地方引用

//        逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    //        @Override
//    public Long[] findCatelogPath(Long catelogId) {
//        List<Long> paths = new ArrayList<>();
//        List<Long> parentPath = findParentPath(catelogId, paths);
//        Collections.reverse(parentPath);
//
//        return parentPath.toArray(new Long[parentPath.size()]);
//    }
////
////    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
//////        收集当前节点id
////        paths.add(catelogId);
////        CategoryEntity byId = this.getById(catelogId);
////        if (byId.getParentCid() != 0) {
////            findParentPath(byId.getParentCid(), paths);
////        }
////        return paths;
////    }
    @Override
    public Long[] findCatelogPath(Long catalogId) {
        List<Long> paths = new ArrayList<>();
        System.out.println("catalogId111===" + paths);
        findParentPath(catalogId, paths);
        Collections.reverse(paths);
        System.out.println("catalogId222===" + paths);
        return paths.toArray(new Long[0]);
    }

    private void findParentPath(Long catalogId, List<Long> paths) {
        System.out.println("catalogId===" + catalogId);
        while (catalogId != 0) {
            paths.add(catalogId);
            CategoryEntity category = getById(catalogId);
            catalogId = category.getParentCid();
        }
    }


    //    recursive lookup
    private List<CategoryEntity> setChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter((menus) -> {
            return menus.getParentCid() == root.getCatId();
        }).map((categoryEntity -> {

//            Find submenus
            categoryEntity.setChildren(setChildren(categoryEntity, all));
            return categoryEntity;
        })).sorted((item1, item2) -> {
            return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getSort() == null ? 0 : item2.getSort());
        }).collect(Collectors.toList());


        return children;
    }

}