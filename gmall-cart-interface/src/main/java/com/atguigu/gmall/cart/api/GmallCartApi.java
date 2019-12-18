package com.atguigu.gmall.cart.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.pojo.UserInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 13:25
 * @version: 1.0
 * @modified By:十一。
 */
public interface GmallCartApi {
    @GetMapping("cart/order/{userId}")
    public Resp<List<Cart>> getAllCarts(@PathVariable("userId") Long userId);
}
