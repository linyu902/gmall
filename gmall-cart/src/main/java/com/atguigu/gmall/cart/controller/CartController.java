package com.atguigu.gmall.cart.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.pojo.UserInfo;
import com.atguigu.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-17 15:12
 * @version: 1.0
 * @modified By:十一。
 */
@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("hello")
    public Resp<Object> hello(){
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        return Resp.ok(null);
    }

    @PostMapping
    public Resp<Object> add(@RequestBody Cart cart){

        this.cartService.add(cart);

        return Resp.ok(null);
    }

    @GetMapping
    public Resp<List<Cart>> list(){

        List<Cart> carts = this.cartService.list();

        return Resp.ok(carts);
    }

    @PostMapping("update")
    private Resp<Object> updateCount(@RequestBody Cart cart){

        this.cartService.updateCount(cart);

        return Resp.ok(null);
    }

    @PostMapping("{skuId}")
    public Resp<Object> deleteBySkuId(@PathVariable("skuId")Long skuId){

        this.cartService.deleteBySkuId(skuId);

        return Resp.ok(null);
    }

    @GetMapping("order/{userId}")
    public Resp<List<Cart>> getAllCarts(@PathVariable("userId") Long userId){
        List<Cart> carts = this.cartService.getAllcarts(userId);
        return Resp.ok(carts);
    }
}
