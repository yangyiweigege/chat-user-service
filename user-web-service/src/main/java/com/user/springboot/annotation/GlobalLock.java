package com.user.springboot.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GlobalLock {

    /**
     * 分布式锁 key的前缀  如 user_1 path = user
     *
     * @return
     */
    String path() default "";

    /**
     * 提取方法入参 具体参数值  格式 #person.name
     *
     * @return
     */
    String key() default "";

    /**
     * 尝试获取 锁 的等待时间
     *
     * @return
     */
    long waitTime() default 0;

    /**
     * 锁的持有时间
     *
     * @return
     */
    long leaseTime() default 120;

    /**
     * 等待时间 持有时间 默认 以秒为单位
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}