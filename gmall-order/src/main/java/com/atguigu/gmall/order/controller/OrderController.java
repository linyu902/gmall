package com.atguigu.gmall.order.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 15:05
 * @version: 1.0
 * @modified By:十一。
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("confirm")
    public Resp<OrderConfirmVO> getConfirm(){
        OrderConfirmVO orderConfirmVO = orderService.getConfirm();
        return Resp.ok(orderConfirmVO);
    }

    @PostMapping("submit")
    public Resp<Object> submit(@RequestBody OrderSubmitVO submitVO){

        Object obj = this.orderService.submit(submitVO);

        return Resp.ok(null);
    }
}
