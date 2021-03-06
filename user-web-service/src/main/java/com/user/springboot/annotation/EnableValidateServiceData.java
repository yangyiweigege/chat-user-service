package com.user.springboot.annotation;

import com.user.springboot.aop.ValidateServiceDataAop;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用该切面 加在启动类上
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ValidateServiceDataAop.class})
public @interface EnableValidateServiceData {
}
