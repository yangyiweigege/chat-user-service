package com.user.springboot.task;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置定时任务
 * @author yangyiwei
 * @date 2018年12月4日
 * @time 下午5:25:45
 */
@Component
@Slf4j
public class ScheduledTask {

	@Autowired
	@Qualifier("threadPoolExecutor")
	private ThreadPoolExecutor executorService;

	@Scheduled(fixedRate = 1000 * 60)
	public void showThreadPoolStatus() {
		log.info("=====================监控线程池运行状况======================");
		log.info("线程池任务总数:{},完成的任务数:{},线程池当前线程数:{},当前正在执行任务的线程数:{}" ,executorService.getTaskCount()
				, executorService.getCompletedTaskCount(), executorService.getPoolSize(), executorService.getActiveCount()
				);
	}
}
