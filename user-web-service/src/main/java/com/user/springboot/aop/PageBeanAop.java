package com.user.springboot.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.chat.springboot.common.annotation.CheckPageBean;
import com.chat.springboot.common.annotation.ValidatePage;

import lombok.extern.slf4j.Slf4j;

/**
 * pageSize pageNo校验
 * 
 * @author yangyiwei
 * @date 2018年6月15日
 * @time 上午9:06:31
 */
@Aspect
@Component
@Slf4j
public class PageBeanAop {

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
	public void pageBeanPointCut() {

	}

	@Around("pageBeanPointCut()")
	public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
		// 判断该方法上 是否有校验pageSize注解
		// Method method = joinPoint.
		Object[] args = joinPoint.getArgs();
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		CheckPageBean checkPageBean = method.getAnnotation(CheckPageBean.class);
		if (checkPageBean != null) { // 方法上存在pageBean注解并且pageSize或者pageNo
																// 为空 则替换参数
			log.info("pageBeanAop执行分页参数校验中.....");
			String argsName[] = methodSignature.getParameterNames();// 获取参数名称
			Class<?> argsType[] = methodSignature.getParameterTypes();
			for (int i = 0; i < argsName.length; i++) {

				if (argsName[i].equals(checkPageBean.currentPage())) { // 如果参数名为pageSize校验是否为空
					if (args[i] == null) { // 参数为空 赋值
						// 此处应当判定具体类型
						log.info("当前参数类型:" + argsType[i]);
						if (argsType[i].getName().equals("java.lang.String")) {
							args[i] = "1";
						} else {
							args[i] = 1;// 默认为
						}
					
					}
				}

				if (argsName[i].equals(checkPageBean.pageSize())) { // 如果参数名为pageNo校验是否为空
					if (args[i] == null) { // 参数为空 赋值
						if (argsType[i].getName().equals("java.lang.String")) {
							args[i] = "10";
						} else {
							args[i] = 10;// 默认为
						}
					}
				}
			}

		}

		// 修改处理后的结果 然后调用 methon.invoke执行

		Object retVal = joinPoint.proceed(args);
		return retVal;

	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * Integer aInteger = 10; if (aInteger instanceof Integer) {
	 * System.out.println("对象空了"); }
	 * 
	 * }
	 */
}
