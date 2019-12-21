package com.atguigu.gmall.oms.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.oms.dao.OrderDao;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.feign.GmallWmsClient;
import com.atguigu.gmall.ums.vo.BoundsVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-20 20:03
 * @version: 1.0
 * @modified By:十一。
 */
@Component
public class OrderCloseListener {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private OrderDao orderDao;

    @RabbitListener(queues = "ORDER-DEAD-QUEUE")
    public void closeOrder(String orderToken){
        // 1.1 关单
        int result = this.orderDao.closeOrder(orderToken);
        if (result == 1){
            // 1.2 发送消息解锁库存
            this.amqpTemplate.convertAndSend("GMALL-STOCK-EXCHANGE","stock.unlock",orderToken);
        }
//        System.out.println("message = " + message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL-ORDER-PAY",durable = "true"),
            exchange = @Exchange(value = "GMALL-ORDER-EXCHANGE",
                                    ignoreDeclarationExceptions = "true",
                                    type = ExchangeTypes.TOPIC),
            key = {"order.pay"}
    ))
    public void OrderPay(String orderToken){

        // 1. 修改订单状态
        Integer result = this.orderDao.setStatus(orderToken);
        if (result == 1){
            // 2. 发送消息减库存
            this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE","stock.minus",orderToken);

            // 3. 发送消息给用户添加积分
            OrderEntity orderEntity = this.orderDao.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderToken));
            BoundsVO boundsVO = new BoundsVO();
            boundsVO.setUserId(orderEntity.getMemberId());
            boundsVO.setGrowth(orderEntity.getGrowth());
            boundsVO.setIntegration(orderEntity.getIntegration());
            this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE","user.bound", JSON.toJSONString(boundsVO));

        }


    }
}
