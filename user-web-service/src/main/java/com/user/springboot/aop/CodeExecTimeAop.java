package com.user.springboot.aop;

import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.user.springboot.cache.JVM_CACHE;
import lombok.extern.slf4j.Slf4j;

/**
 * 监控每段代码执行时间
 * 
 * @author yangyiwei
 * @date 2018年11月10日
 * @time 下午4:58:35
 */
@Aspect
@Component
@Slf4j
@Order(2)//代码执行时间计算顺序2
public class CodeExecTimeAop {

	/**
	 * <pre>
	 * 功       能: 统一定义一个切面，复用
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年3月9日 上午9:42:27
	 * Q    Q: 2873824885
	 * </pre>
	 */
	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)")
	public void codeExecTimeAop() {

	}

	@Around("codeExecTimeAop()")
	public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis(); // 获取开始时间
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) { //如果获取不到url,则有可能是rpc调用，直接返回结果
			return joinPoint.proceed(joinPoint.getArgs());
		}
		HttpServletRequest request = attributes.getRequest();
		// 修改处理后的结果 然后调用 methon.invoke执行
		Object retVal = joinPoint.proceed(joinPoint.getArgs());
		long endTime = System.currentTimeMillis(); // 获取结束时间
		// 记录接口被访问频率 以及代码执行时间
		String url = request.getRequestURI();
		log.info("用户ip地址:{},...访问的url:{},.....耗时:{}", url, request.getRequestURI(),
				(endTime - startTime) + "ms");
		if ((endTime - startTime) > 2000) { //执行时间较慢接口 记录下来
			JVM_CACHE.EXEC_SLOW_INTERFACE.put(url, (int) (endTime - startTime));
			log.warn("{}接口比较耗时...建议优化....", url);
		}
		

		if (JVM_CACHE.COUNT_INTERFACE.get(url) == null) { //dcl确保每个接口写入 只初始化一次
			synchronized (this) {
				if (JVM_CACHE.COUNT_INTERFACE.get(url) == null) { //为空则写入统计
					JVM_CACHE.COUNT_INTERFACE.put(url, new AtomicInteger(1));//第一次访问
					log.info("接口:{}, 累计访问次数:{}",url ,1);
				} else {
					log.info("接口:{}, 累计访问次数:{}",url ,JVM_CACHE.COUNT_INTERFACE.get(url).incrementAndGet());//原子类执行接口访问次数+1
				}
			}
		} else { //初始化完成后，以后只需要统计次数
			log.info("接口:{}, 累计访问次数:{}",url ,JVM_CACHE.COUNT_INTERFACE.get(url).incrementAndGet());//原子类执行接口访问次数+1
		}
		
		//这个地方 可以把接口名 执行时间 什么时候调用的 等信息 写入数据库
		return retVal;
	}

}
