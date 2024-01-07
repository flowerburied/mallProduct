package com.example.mall.search.controller;

import com.example.mall.search.service.MallSearchService;
import com.example.mall.search.vo.SearchParam;
import com.example.mall.search.vo.SearchResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

@Controller
public class SearchController {

    @Resource
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model) {
        SearchResponse list = mallSearchService.search(searchParam);
        model.addAttribute("result", list);
        return "list";
    }
}
