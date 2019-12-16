package com.atguigu.gmall.item.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-14 13:02
 * @version: 1.0
 * @modified By:十一。
 */
@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("{skuId}")
    public Resp<ItemVO> loadItem(@PathVariable("skuId") Long skuId) throws ExecutionException, InterruptedException {
        ItemVO itemVO = itemService.loadItem(skuId);

        return Resp.ok(itemVO);
    }
}
