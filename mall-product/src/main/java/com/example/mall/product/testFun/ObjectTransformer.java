package com.example.mall.product.testFun;

import com.example.mall.product.service.SkuSaleAttrValueService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectTransformer {

    public static void main(String[] args) {

    }

    public static void mainss(String[] args) {
        // 原始对象列表
        List<Map<String, Object>> originalList = new ArrayList<>();
        originalList.add(createObject(7, 5, "parameters001", "666"));
        originalList.add(createObject(8, 5, "parameters001", "666"));
        originalList.add(createObject(9, 5, "parameters001", "999"));
        originalList.add(createObject(10, 5, "parameters001", "999"));
        originalList.add(createObject(8, 9, "parameters002", "1000"));
        originalList.add(createObject(10, 9, "parameters002", "1000"));
        System.out.println("originalList" + originalList);
        // 转换后的对象列表
        List<Map<String, Object>> transformedList = transformList(originalList);
        System.out.println("transformedList" + transformedList);
        // 打印结果
        for (Map<String, Object> transformedObject : transformedList) {
            System.out.println(transformedObject);
        }
    }

    private static Map<String, Object> createObject(int skuId, int attrId, String attrName, String attrValue) {
        Map<String, Object> object = new HashMap<>();
        object.put("skuId", skuId);
        object.put("attrId", attrId);
        object.put("attrName", attrName);
        object.put("attrValue", attrValue);
        return object;
    }

    private static List<Map<String, Object>> transformList(List<Map<String, Object>> originalList) {
        Map<String, Map<String, Object>> resultMap = new HashMap<>();

        for (Map<String, Object> originalObject : originalList) {
            String key = originalObject.get("attrId")
                    + "_" + originalObject.get("attrName")
                    + "_" + originalObject.get("attrValue");
            Map<String, Object> stringObjectMap = resultMap.computeIfAbsent(key, k -> new HashMap<>());
System.out.println("stringObjectMap"+stringObjectMap);
            stringObjectMap.put(
                    "skuIds",resultMap.getOrDefault(key, new HashMap<>()).getOrDefault("skuIds", "")
                            + ","
                            + originalObject.get("skuId"));
            resultMap.put(key, resultMap.get(key));
        }

        return new ArrayList<>(resultMap.values());
    }
}
