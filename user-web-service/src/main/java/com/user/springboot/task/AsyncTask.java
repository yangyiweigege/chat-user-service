package com.user.springboot.task;

import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

@Component 
public class AsyncTask {
	
	@Async
	public Future<Boolean> doTask11() throws Exception{
		long startTime = System.currentTimeMillis();
		Thread.sleep(1000);
		long endTime = System.currentTimeMillis();
		System.out.println("任务1耗时:" + (endTime - startTime));
		return new AsyncResult<Boolean>(true);	
	}
	
	@Async
	public Future<Boolean> doTask22() throws Exception{
		long startTime = System.currentTimeMillis();
		Thread.sleep(600);
		long endTime = System.currentTimeMillis();
		System.out.println("任务2耗时:" + (endTime - startTime));
		return new AsyncResult<Boolean>(true);	
	}
	
	@Async
	public Future<Boolean> doTask33() throws Exception{
		long startTime = System.currentTimeMillis();
		Thread.sleep(700);
		long endTime = System.currentTimeMillis();
		System.out.println("任务3耗时:" + (endTime - startTime));
		return new AsyncResult<Boolean>(true);	
	}
 
}
