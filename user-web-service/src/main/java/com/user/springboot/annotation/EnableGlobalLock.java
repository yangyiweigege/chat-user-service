package com.user.springboot.annotation;

import com.user.springboot.aop.GlobalLockAop;
import com.user.springboot.aop.ValidateServiceDataAop;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用分布式锁切面 加在启动类上
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({GlobalLockAop.class})
public @interface EnableGlobalLock {
}
