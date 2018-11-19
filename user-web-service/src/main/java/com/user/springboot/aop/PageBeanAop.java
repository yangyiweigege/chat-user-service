package com.user.springboot.aop;

import java.lang.reflect.Method;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.chat.springboot.common.annotation.ValidatePage;




/**
 * pageSize pageNo校验
 * @author yangyiwei
 * @date 2018年6月15日
 * @time 上午9:06:31
 */
//@Aspect
//@Component
public class PageBeanAop {
	
	private final static Logger logger = Logger.getLogger(PageBeanAop.class);

	/**
	 * <pre>
	 * 功       能: 统一定义一个切面，复用
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年3月9日 上午9:42:27
	 * Q    Q: 2873824885
	 * </pre>
	 */
	@Pointcut("execution(public * com.weige.ssm.controller.*.*(..))")
	public void pageBeanPointCut() {

	}

	@Around("pageBeanPointCut()")
	public Object log(ProceedingJoinPoint joinPoint) {
		System.out.println("pageBeanAop执行校验中..........");
		// 判断该方法上 是否有校验pageSize注解
		// Method method = joinPoint.
		Object[] args = joinPoint.getArgs();
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		if (method.getAnnotation(ValidatePage.class) != null) { // 方法上存在pageBean注解并且pageSize或者pageNo
																// 为空 则替换参数
			logger.info("pageBeanAop执行分页参数校验中.....");
			String argsName[] = methodSignature.getParameterNames();// 获取参数名称
			for (int i = 0; i < argsName.length; i++) {

				if (argsName[i].equals("pageSize")) { // 如果参数名为pageSize校验是否为空
					if (args[i] == null) { // 参数为空 赋值
						args[i] = 10;// 默认为10
					}
				}

				if (argsName[i].equals("pageNo")) { // 如果参数名为pageNo校验是否为空
					if (args[i] == null) { // 参数为空 赋值
						args[i] = 1;// 默认为10
					}
				}
			}

		}

		// 修改处理后的结果 然后调用 methon.invoke执行

		try {
			Object retVal = joinPoint.proceed(args);
			return retVal;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*public static void main(String[] args) {

		Integer aInteger = 10;
		if (aInteger instanceof Integer) {
			System.out.println("对象空了");
		}

	}*/
}
