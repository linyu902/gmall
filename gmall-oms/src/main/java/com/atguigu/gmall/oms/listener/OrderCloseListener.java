package com.atguigu.gmall.oms.listener;

import com.atguigu.gmall.oms.dao.OrderDao;
import com.atguigu.gmall.oms.feign.GmallWmsClient;
import org.springframework.amqp.core.AmqpTemplate;
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
}
