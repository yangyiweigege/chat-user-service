package com.user.springboot.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.chat.springboot.common.TraceThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置线程池
 * 
 * @author yangyiwei
 * @date 2018年12月4日
 * @time 下午4:13:19
 */
@Configuration
//@ConfigurationProperties(prefix = "thread.pool")
@PropertySource("classpath:threadPool.property")
@Slf4j
public class ThreadPoolConfig {

	/**
	 * 给自己的线程池设置名字
	 * @param coreSize
	 * @param MaxSize
	 * @param waitSize
	 * @param keepAliveTime
	 * @return
	 */
	@Bean("threadPoolExecutor")
	public ThreadPoolExecutor getThreadPool(@Value("${thread.pool.core.size}") int coreSize,
			@Value("${thread.pool.max.size}") int MaxSize, @Value("${thread.pool.queen.size}") int waitSize,
			@Value("${thread.pool.keep.time}") int keepAliveTime) {
		final AtomicInteger atomicInteger = new AtomicInteger();
		return new ThreadPoolExecutor(coreSize, MaxSize, keepAliveTime, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(waitSize),(Runnable r) -> {
					Thread thread = new Thread(r, "controller-pool-" + atomicInteger.incrementAndGet());
					log.info("mongodb controller create thread....." + thread.getName());
					return thread;
				}, new ThreadPoolExecutor.CallerRunsPolicy() );
	}
	
	/**
	 * 配置自定义线程池(定制了异常捕捉功能)
	 * @param coreSize
	 * @param MaxSize
	 * @param waitSize
	 * @param keepAliveTime
	 * @return
	 */
	@Bean("traceThreadPoolExecutor")
	public ThreadPoolExecutor getTraceThreadPool(@Value("${thread.pool.core.size}") int coreSize,
			@Value("${thread.pool.max.size}") int MaxSize, @Value("${thread.pool.queen.size}") int waitSize,
			@Value("${thread.pool.keep.time}") int keepAliveTime) {
		return new TraceThreadPoolExecutor(coreSize, MaxSize, keepAliveTime, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(waitSize));
	}

}
