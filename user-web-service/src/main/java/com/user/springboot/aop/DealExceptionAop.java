package com.user.springboot.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * 捕捉代码异常
 * 
 * @author yangyiwei
 * @date 2018年11月19日
 * @time 下午4:02:44
 */
//@Aspect
//@Component
@Slf4j
//@Order(1)//确保异常捕捉机制属于最顶层
public class DealExceptionAop {

	/**
	 * <pre>
	 * 功       能: 统一定义一个切面，复用
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年3月9日 上午9:42:27
	 * Q    Q: 2873824885
	 * </pre>
	 */
	@Pointcut("execution(public * com.user.springboot.controller.*.*(..))")
	public void dealExceptionAop () {

	}

	@Around("dealExceptionAop()")
	public Object catchExceptionAndDeal(ProceedingJoinPoint joinPoint) {
		// 修改处理后的结果 然后调用 methon.invoke执行
		Object retVal;
		try {
			retVal = joinPoint.proceed(joinPoint.getArgs());
			return retVal;
		} catch (Throwable e) {
			e.printStackTrace();
			log.error("出现了系统未知的错误-----！！！！", e);
			return new ResponseResult<>(ResultStatus.UNKNOW_ERROR);
		} 
		
	}

}
