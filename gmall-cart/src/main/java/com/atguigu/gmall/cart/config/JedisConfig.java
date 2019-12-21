package com.atguigu.gmall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-20 17:51
 * @version: 1.0
 * @modified By:十一。
 */
@Component
public class JedisConfig {

    @Bean
    public JedisPool jedisPool(){
        return new JedisPool("192.168.79.128");
    }
}
