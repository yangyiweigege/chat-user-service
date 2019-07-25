package com.user.springboot.annotation;

import java.lang.annotation.*;

/**
 * 分布式锁注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GlobalLock {

    String path() default "";

    String key() default "";

    long waitTime() default 0;

    long leaseTime() default 120;

}