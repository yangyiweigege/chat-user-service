package com.user.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 初始化工作 可以在此类完成
 */
@Component
@Slf4j
public class InitInterfaceInvokeCount implements CommandLineRunner  {
	
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * 启动时初始化所有接口访问次数
	 */
	@Override
	public void run(String... args) throws Exception {
		log.info("初始化所有接口访问次数.....");
		//DispatcherServlet
	}


}
