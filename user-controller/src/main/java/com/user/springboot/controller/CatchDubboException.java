package com.user.springboot.controller;

import java.lang.annotation.*;

/**
 * 在dubbo方法上加此注解。捕获异常 转化为CommonResult
 * 注意事项：
 * 1.接口返回必须是:commonResult  否则会报 java.lang.ClassCastException: XXXClass cannot be cast to CommonResult
 * 2.加了该注解的接口 不可以存在嵌套事务 否则会报  Transaction rolled back because it has been marked as rollback-only
 * 例子：如A方法存在事物注解 调用加了该注解的dubbo接口。
 * 原因:由于子事物已经被回滚 spring会标志当前事物已回滚 然后调用该方法的 A方法 继续commit事务 就会报错
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CatchDubboException {

    /**
     * 可指定捕捉异常类型(待有缘人实现)
     * @return
     */
    String value() default "";
}
