package com.atguigu.gmall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 18:20
 * @version: 1.0
 * @modified By:十一。
 */
@Configuration
public class ThreadExceptorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(100,200,60, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10000));
    }
}
