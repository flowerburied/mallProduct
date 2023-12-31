package com.example.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.mall.product.service.CategoryBrandRelationService;
import com.example.mall.product.vo.Catelog2Vo;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.product.dao.CategoryDao;
import com.example.mall.product.entity.CategoryEntity;
import com.example.mall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    //    @Resource
//    CategoryDao categoryDao;
    @Resource
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Resource
    CategoryBrandRelationService categoryBrandRelationService;

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

    /**
     * 级联更新所有数据
     *
     * @param category
     */
//    @CacheEvict(value = {"category"}, key = "'getLevel1'")
//    @Caching(evict = {
//            @CacheEvict(value = {"category"}, key = "'getLevel1'"),
//            @CacheEvict(value = {"category"}, key = "'getCatelogJson'")
//    })
    @CacheEvict(value = {"category"}, allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        baseMapper.updateById(category);

        categoryBrandRelationService.updateCatelog(category);


    }

    //需要指定缓存的分区（按照业务划分）
    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)  //代表当前方法的结果需要缓存，如果缓存中有，方法不调用，如果缓存中没有调用方法，最后将结果返回缓存
    @Override
    public List<CategoryEntity> getLevel1() {
        long l = System.currentTimeMillis();
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        System.out.println("消耗时间：{}" + (System.currentTimeMillis() - l));
        return categoryEntities;
    }

    @Cacheable(value = {"category"}, key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {

        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1.查出所有1级分类
        List<CategoryEntity> level1 = getParent_cid(selectList, 0L);
        //2.封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    //1.查出1级分类中所有2级分类
                    List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
                    //2.封装上面的结果
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                            //查询当前2级分类的3级分类
                            List<CategoryEntity> level3 = getParent_cid(selectList, l2.getCatId());
                            if (level3 != null) {
                                List<Catelog2Vo.Catelog3Vo> collect = level3.stream().map(l3 -> {
                                    //封装指定格式
                                    Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                    return catelog3Vo;
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(collect);
                            }
                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catelog2Vos;
                }
        ));


        return parent_cid;

    }


    public Map<String, List<Catelog2Vo>> getCatelogJson2() {

        // 1.从缓存中读取分类信息
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
//not in cache
            Map<String, List<Catelog2Vo>> cateLogJsonFromDb = getCateLogJsonFromDbWithRedisLock();
            return cateLogJsonFromDb;
        }
        Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });

        return stringListMap;
    }


    /**
     * 加缓存前,只读取数据库的操作
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCateLogJsonFromDb() {

        /**
         * 优化:将数据库中的多次查询变为一次,存至缓存selectList,需要的数据从list取出,避免频繁的数据库交互
         * 只要是同一把锁，就能锁住，需要这个锁的所有线程
         *
         */
        synchronized (this) {
//            得到锁后应该去缓存确认一次，没有才继续查询
            return getDataFromDb();

        }
    }

    /**
     * 缓存中的数据如何和数据库保持一致
     * 缓存数据一致性
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCateLogJsonFromDbWithRedisLock() {

        //注意锁的名字
        RLock lock = redissonClient.getLock("cateLogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }


        return dataFromDb;


    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });

            return stringListMap;
        }

        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1.查出所有1级分类
        List<CategoryEntity> level1 = getParent_cid(selectList, 0L);
        //2.封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    //1.查出1级分类中所有2级分类
                    List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
                    //2.封装上面的结果
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                            //查询当前2级分类的3级分类
                            List<CategoryEntity> level3 = getParent_cid(selectList, l2.getCatId());
                            if (level3 != null) {
                                List<Catelog2Vo.Catelog3Vo> collect = level3.stream().map(l3 -> {
                                    //封装指定格式
                                    Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                    return catelog3Vo;
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(collect);
                            }
                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catelog2Vos;
                }
        ));

//retrieve data and place it in cache
        redisTemplate.opsForValue().set("catalogJSON", JSON.toJSONString(parent_cid), 1, TimeUnit.DAYS);

        return parent_cid;
    }

    public Map<String, List<Catelog2Vo>> getCateLogJsonFromDbWithLocalLock() {
        synchronized (this) {
//            得到锁后应该去缓存确认一次，没有才继续查询
            return getDataFromDb();

        }
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
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