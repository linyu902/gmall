package com.atguigu.gmall.order.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.order.pay.AlipayTemplate;
import com.atguigu.gmall.order.pay.PayAsyncVo;
import com.atguigu.gmall.order.pay.PayVo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import org.springframework.amqp.core.AmqpTemplate;
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

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @GetMapping("confirm")
    public Resp<OrderConfirmVO> getConfirm(){
        OrderConfirmVO orderConfirmVO = orderService.getConfirm();
        return Resp.ok(orderConfirmVO);
    }

    @PostMapping("submit")
    public Resp<Object> submit(@RequestBody OrderSubmitVO submitVO){

        OrderEntity orderEntity = this.orderService.submit(submitVO);

        try {
            PayVo payVo = new PayVo();
            payVo.setOut_trade_no(orderEntity.getOrderSn());
            payVo.setSubject("谷粒商城");
            payVo.setTotal_amount(orderEntity.getTotalAmount().intValue() == 0? "100" : orderEntity.getTotalAmount().toString());
            payVo.setBody("咱俩完了，真的。");
            String form = alipayTemplate.pay(payVo);
            System.out.println("form = " + form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return Resp.ok(null);
    }

    @PostMapping("pay/success")
    public Resp<Object> paySuccess(PayAsyncVo payAsyncVo){

        String orderToken = payAsyncVo.getOut_trade_no();

        this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE","order.pay",orderToken);

        return Resp.ok(null);
    }
}
