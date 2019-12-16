package com.atguigu.gmall.index.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-14 11:27
 * @version: 1.0
 * @modified By:十一。
 */
@Target( ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {

    String prefix() default "";

    String timeout() default "5";

    String random() default "5";
}
