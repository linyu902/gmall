package com.atguigu.gmall.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.vo.SaleVO;
import com.atguigu.gmall.vo.SkuSmsVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-04 20:07
 * @version: 1.0
 * @modified By:十一。
 */
public interface GmallSmsApi {
    @PostMapping("/sms/skubounds/skusale/save")
    public Resp<Object> saveSms(@RequestBody SkuSmsVO skuSmsVO);

    @GetMapping("sms/skubounds/{skuId}")
    public Resp<List<SaleVO>> queryBoundsAndFullAndLadder(@PathVariable("skuId") Long skuId);
}
