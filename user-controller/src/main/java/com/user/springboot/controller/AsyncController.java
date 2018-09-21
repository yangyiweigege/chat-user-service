package com.user.springboot.controller;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chat.springboot.common.AsyncTask;


/**
 * <pre>
 * 功       能: 异步任务控制层
 * 涉及版本: V3.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2018年4月2日 下午8:49:02  
 * Q    Q: 2873824885
 * </pre>
 */
//@RestController
@RequestMapping("/async")
public class AsyncController {
	@Autowired
	private AsyncTask asyncTask;
	
	@RequestMapping(value = "/task", method = {RequestMethod.GET, RequestMethod.POST})
	public String taskTest() throws Exception {
		long startTime = System.currentTimeMillis();
		Future<Boolean> a = asyncTask.doTask11();
		Future<Boolean> b = asyncTask.doTask22();
		Future<Boolean> c = asyncTask.doTask33();
		while (!a.isDone() || !b.isDone() || !c.isDone()) {
			System.out.println("有任务还没有完成");//直到三个任务完成 跳出任务
		}
		long endTime = System.currentTimeMillis();
		String message = "此任务 一共耗时： " + (endTime - startTime) + "秒";
		return message;
		
	}
}
