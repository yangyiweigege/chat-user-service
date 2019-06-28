package com.user.springboot.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验参数
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckParam {

    /**
     * 要校验的参数 属性 如果是基本类型 无需填写
     * @return
     */
    String[] attributes() default {};
}
