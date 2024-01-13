package com.example.mall.search.controller;

import com.example.mall.search.service.MallSearchService;
import com.example.mall.search.vo.SearchParam;
import com.example.mall.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {

    @Resource
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request) {
//        System.out.println("searchParam===" + searchParam);
        String queryString = request.getQueryString();
        searchParam.set_queryString(queryString);
        SearchResult list = mallSearchService.search(searchParam);
        System.out.println("list===" + list);
        model.addAttribute("result", list);
        return "list";
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        //只有锁名字一样,就是同一把锁

        return "hello";
    }
}
