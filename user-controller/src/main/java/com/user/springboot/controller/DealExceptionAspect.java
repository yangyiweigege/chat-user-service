package com.user.springboot.controller;

import com.tuya.crm.client.domain.common.CommonResult;
import com.tuya.crm.common.exception.BizException;
import com.tuya.crm.common.exception.BizExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 捕捉代码异常 但无法捕捉到拦截器异常
 * 
 * @author yangyiwei
 * @date 2018年11月19日
 * @time 下午4:02:44
 */
@Aspect
@Component
@Slf4j
@Order(Integer.MIN_VALUE) //确保异常捕捉机制属于最顶层,在事物回滚 或者 提交后 触发这个切面
public class DealExceptionAspect {

	/**
	 * 定义切入点 捕捉catchDubboException
	 */
	@Pointcut("@annotation(com.tuya.crm.core.advice.CatchDubboException)")
	public void dealExceptionAop () {
	}

	@Around("dealExceptionAop()")
	public Object catchExceptionAndDeal(ProceedingJoinPoint joinPoint) {
		Object retVal;
		try {
			log.info("执行方法异常捕捉切面...");
			retVal = joinPoint.proceed(joinPoint.getArgs());
			return retVal;
		} catch (Throwable e) {
			if (e instanceof BizException) { //此处自定义项目异常
				BizException bizException = (BizException) e;
				return CommonResult.newInstance(bizException.getCode(),bizException.getMessage());
			}
			log.error("服务器发生未知错误======!!", e);
			return CommonResult.newInstance(BizExceptionCode.SYS_UNKNOWN_EXCEPTION.getCode(),BizExceptionCode.SYS_UNKNOWN_EXCEPTION.getMessage());
		}
	}

}
