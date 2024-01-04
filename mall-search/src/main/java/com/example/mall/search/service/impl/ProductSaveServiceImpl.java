package com.example.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.common.constant.EsConstant;
import com.example.common.to.es.SkuEsModel;
import com.example.mall.search.config.MallElasticSearchConfig;
import com.example.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {


    @Resource
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //save to elasticSearch
//        es creat index
//        BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();

        skuEsModels.forEach(item -> {
//            structure save request
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(item.getSkuId().toString());
            String s = JSON.toJSONString(item);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);

        });


        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, MallElasticSearchConfig.COMMON_OPTIONS);

        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());

//        log.error("商品上架错误：{}", collect);
        log.info("商品上架完成:{},返回数据:{},", collect, bulk.toString());
        return b;
    }
}
