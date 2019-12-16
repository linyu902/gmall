package com.atguigu.gmall.index.annotation;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-14 11:30
 * @version: 1.0
 * @modified By:十一。
 */
@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(com.atguigu.gmall.index.annotation.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable{

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        //获取注解
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);
        //获取前缀
        String prefix = gmallCache.prefix();
        //组装key
        String key = prefix + Arrays.asList(joinPoint.getArgs()).toString();
        //查询缓存
        Object cacheHit = this.cacheHit(key, signature);
        if (cacheHit != null){
            return cacheHit;
        }
        //获取分布式锁
        RLock lock = redissonClient.getLock("lock" + Arrays.asList(joinPoint.getArgs()).toString());

        lock.lock();
        //再查一次缓存，防止加锁过程中已有别的线程写入
        cacheHit = this.cacheHit(key,signature);
        if (cacheHit != null){
            lock.unlock();
            return cacheHit;
        }
        //查询数据库
        Object result = joinPoint.proceed(joinPoint.getArgs());
        //将结果放入缓存
        redisTemplate.opsForValue().set(key,JSON.toJSONString(result),7l + new Random().nextInt(5), TimeUnit.DAYS);
        //返回结果
        lock.unlock();
        return result;
    }

    private Object cacheHit(String key , MethodSignature signature){
        
        String jsonStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(jsonStr)){
            //获取方法返回值类型
            Class returnType = signature.getReturnType();
            return JSON.parseObject(jsonStr, returnType);
        }
        return null;
    }
}
