package com.atguigu.gmall.search.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseVO;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-10 15:25
 * @version: 1.0
 * @modified By:十一。
 */
@RequestMapping("search")
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public Resp<SearchResponseVO> search(SearchParam searchParam) throws IOException {
        SearchResponseVO responseVO = searchService.search(searchParam);

        return Resp.ok(responseVO);
    }

}
