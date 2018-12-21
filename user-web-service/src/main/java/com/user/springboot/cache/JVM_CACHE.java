package com.user.springboot.cache;

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
	



	
}
