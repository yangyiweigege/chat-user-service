package com.user.springboot.aop;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 记录下所有的访问情况
 * 
 * @author yangyiwei
 * @date 2018年6月4日
 * @time 下午3:47:57
 */
@Aspect
@Component
public class AccessLogAop {

	private final static Logger logger = Logger.getLogger(AccessLogAop.class);
	
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
	public void logPointCut() {

	}

	@Before("logPointCut()")
	public void log(JoinPoint joinPoint) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		// 记录接口被访问频率
		logger.info("ip地址:" + request.getRemoteAddr() + "...访问的url:" + request.getRequestURI());
		/*
		 * // 类方法 logger.info("class : " +
		 * joinPoint.getSignature().getDeclaringTypeName() + " method:" +
		 * joinPoint.getSignature().getName()); // 参数 logger.info("agr :" +
		 * joinPoint.getArgs());
		 */
	}

	/*
	 * @AfterReturning(value = "logPointCut()", returning = "object") public
	 * void getId(Object object) {
	 * 
	 * }
	 */
}
