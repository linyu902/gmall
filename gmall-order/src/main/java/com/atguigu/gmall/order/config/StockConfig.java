package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-20 19:51
 * @version: 1.0
 * @modified By:十一。
 */
@Configuration
public class StockConfig {

    /**
     * 延迟队列
     * @return
     */
    @Bean("WMS-TTL-QUEUE")
    public Queue ttlQueue(){

        Map<String,Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", "GMALL.STOCK.UNLOCK");
        map.put("x-dead-letter-routing-key", "stock.unlock");
        map.put("x-message-ttl", 9000); // 仅仅用于测试，实际根据需求，通常30分钟或者15分
        return new Queue("WMS-TTL-QUEUE",true,false,false,map);
    }

    /**
     * 延迟队列绑定到交换机
     * @return
     */
    @Bean("WMS-TTL-BINDING")
    public Binding ttlBinding(){

        return new Binding("WMS-TTL-QUEUE",Binding.DestinationType.QUEUE,"GMALL.STOCK.UNLOCK","stock.ttl",null);
    }
}
