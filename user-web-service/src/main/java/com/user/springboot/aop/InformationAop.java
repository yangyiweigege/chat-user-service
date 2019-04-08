package com.user.springboot.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 消息通知 写入redis
 * 
 * @author yangyiwei
 * @date 2018年7月4日
 * @time 上午9:47:34
 */
@Component
@Aspect
public class InformationAop {

	/**
	 * <pre>
	 * 功       能: 统一定义一个切面，复用
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年3月9日 上午9:42:27
	 * Q    Q: 2873824885
	 * </pre>
	 */
	@Pointcut("execution(public * com.user.springboot.controller.UserInfoController.*(..))")
	public void logPointCut() {

	}

	@Before("logPointCut()")
	public void before(JoinPoint joinPoint) {
		System.out.println("[Aspect1] before advise");
		//try {int i = 1 / 0;
		// } catch (Exception e) {
		//   log.info("发生错误，跳过aop执行");
		//}

	}

	@AfterReturning(value = "logPointCut()", returning = "object")
	public void getId(Object object) {
		
	}

}
