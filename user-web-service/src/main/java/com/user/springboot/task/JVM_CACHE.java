package com.user.springboot.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 定义本地缓存
 * @author yangyiwei
 * @date 2018年12月15日
 * @time 下午2:33:48
 */
public class JVM_CACHE {
	
	/**
	 * 记录每个接口访问次数
	 */
	public static Map<String, Integer> errorInterface = new ConcurrentHashMap<>();
	
	/**
	 * 记录每个接口访问次数
	 */
	public static Map<String, AtomicInteger> countInterface = new ConcurrentHashMap<>();

	/**
	 * 记录最近一次慢执行接口时间
	 */
	public static Map<String, Integer> execSlowInterface = new ConcurrentHashMap<>();
	
	/**
	 * 定义成品批缓存
	 */
	public static Map<String, Object> productionCache = new ConcurrentHashMap<>();
	
	/**
	 * 定义生产批缓存
	 */
	public static Map<String, Object> productCache = new ConcurrentHashMap<>();


	
}
