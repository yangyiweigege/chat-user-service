package com.user.springboot.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 在方法上加此注解,即可进行入参校验
 * 校验 对象, 包装类型的属性(不包含集合)
 * 若校验集合 配合@PerItem注解使用
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateServiceData {

    /**
     * 填写校验的属性名(不要包含集合中对象属性)
     * @return 校验的属性名
     */
    String[] attributes() default {};

    /**
     * 是否开启CheckParam注解
     * 开启后 将直接取参数上的CheckParam做校验
     * @return
     */
    boolean openCheckParam() default false;
}
