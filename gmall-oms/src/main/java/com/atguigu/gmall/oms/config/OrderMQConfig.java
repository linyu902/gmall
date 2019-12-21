package com.atguigu.gmall.oms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
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
@Component
public class OrderMQConfig {

    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange exchange(){

        return new TopicExchange("GMALL-ORDER-EXCHANGE", true, false, null);
    }

    /**
     * 延迟队列
     * @return
     */
    @Bean("ORDER-TTL-QUEUE")
    public Queue ttlQueue(){

        Map<String,Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", "GMALL-ORDER-EXCHANGE");
        map.put("x-dead-letter-routing-key", "order.dead");
        map.put("x-message-ttl", 120000); // 仅仅用于测试，实际根据需求，通常30分钟或者15分
        return new Queue("ORDER-TTL-QUEUE",true,false,false,map);
    }

    /**
     * 延迟队列绑定到交换机
     * @return
     */
    @Bean("ORDER-TTL-BINDING")
    public Binding ttlBinding(){

        return new Binding("ORDER-TTL-QUEUE",Binding.DestinationType.QUEUE,"GMALL-ORDER-EXCHANGE","order.ttl",null);
    }

    /**
     * 死信队列
     * @return
     */
    @Bean("ORDER-DEAD-QUEUE")
    public Queue deadQueue(){

        return new Queue("ORDER-DEAD-QUEUE",true,false,false,null);
    }

    @Bean("ORDER-DEAD-BINDING")
    public Binding deadBinding(){

        return new Binding("ORDER-DEAD-QUEUE",Binding.DestinationType.QUEUE,"GMALL-ORDER-EXCHANGE","order.dead",null);
    }
}
