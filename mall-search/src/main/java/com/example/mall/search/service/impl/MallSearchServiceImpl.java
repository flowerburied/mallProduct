package com.example.mall.search.service.impl;

import com.example.mall.search.service.MallSearchService;
import com.example.mall.search.vo.SearchParam;
import com.example.mall.search.vo.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MallSearchServiceImpl implements MallSearchService {


    @Resource
    RestHighLevelClient restHighLevelClient;
    //去es检索
    @Override
    public SearchResponse search(SearchParam searchParam) {


        return null;
    }
}
