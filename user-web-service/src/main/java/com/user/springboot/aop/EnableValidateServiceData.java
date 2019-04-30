package com.user.springboot.aop;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 杨哥
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ValidateServiceDataAspect.class})
public @interface EnableValidateServiceData {
}
