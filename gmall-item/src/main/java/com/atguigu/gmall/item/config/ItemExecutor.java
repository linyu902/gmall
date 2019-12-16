package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-16 11:43
 * @version: 1.0
 * @modified By:十一。
 */
@Configuration
public class ItemExecutor {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(8000,10000,30, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10000));
    }
}
