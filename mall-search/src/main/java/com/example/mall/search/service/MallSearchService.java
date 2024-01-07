package com.example.mall.search.service;

import com.example.mall.search.vo.SearchParam;
import com.example.mall.search.vo.SearchResult;

public interface MallSearchService {
    /**
     * @param searchParam 检索所有参数
     * @return 返回所有结果
     */
    SearchResult search(SearchParam searchParam);
}
