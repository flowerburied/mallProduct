package com.example.mall.search.service;

import com.example.mall.search.vo.SearchParam;
import com.example.mall.search.vo.SearchResponse;

public interface MallSearchService {
    /**
     * @param searchParam 检索所有参数
     * @return 返回所有结果
     */
    SearchResponse search(SearchParam searchParam);
}
