package com.atguigu.gmall.oms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 13:24
 * @version: 1.0
 * @modified By:十一。
 */
public interface GmallOmsApi {
    @PostMapping("oms/order")
    public Resp<OrderEntity> bigSave(@RequestBody OrderSubmitVO orderSubmitVO);

    @GetMapping("oms/orderitem/{orderToken}")
    public Resp<OrderItemEntity> querySeckillOrder(@PathVariable("orderToken")String orderToken);
}
