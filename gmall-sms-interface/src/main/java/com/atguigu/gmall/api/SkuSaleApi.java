package com.atguigu.gmall.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.vo.SkuSmsVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-04 20:07
 * @version: 1.0
 * @modified By:十一。
 */
public interface SkuSaleApi {
    @PostMapping("/sms/skubounds/skusale/save")
    public Resp<Object> saveSms(@RequestBody SkuSmsVO skuSmsVO);
}
