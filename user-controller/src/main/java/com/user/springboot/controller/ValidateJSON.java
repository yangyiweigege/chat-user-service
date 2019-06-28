package com.user.springboot.controller;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验 RequestBody注解的对象
 * 该注解作用范围仅限于Controller层
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateJSON {

	/**
	 * 需要校验的属性名称
	 * @return
	 */
	 String[] attributes() default {};
}
