package com.user.springboot.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chat.springboot.common.jms.LogProducer;
import com.chat.springboot.common.jms.LogTopicProducer;
import com.chat.springboot.common.response.Result;
import com.chat.springboot.common.response.ResultStatus;




/**
 * <pre>
 * 功       能: 消息中间件
 * 涉及版本: V3.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2018年5月3日 上午9:47:10
 * Q    Q: 2873824885
 * </pre>
 */
//@Controller
//@RequestMapping("/message")
public class ActiveMQController {
	
	@Autowired
	private LogProducer logProducer;
	@Autowired
	private LogTopicProducer logTopicProduer;
	
	@RequestMapping(value = "/write/queue/{message}", method = RequestMethod.GET)
	@ResponseBody
	public Result<Object> writeQueueMessage(@PathVariable("message") String message) throws Exception {	
		logProducer.run("我要写入日志的内容===(队列)", message);
		return new Result<Object>().setCode(ResultStatus.SUCCESS).setData("队列日志写入成功");
	}
	
	@RequestMapping(value = "/write/topic/{message}", method = RequestMethod.GET)
	@ResponseBody
	public Result<Object> writeTopicMessage(@PathVariable("message") String message) throws Exception {
		logTopicProduer.run("我要写入日志的内容===(主题)", message);
		return new Result<Object>().setCode(ResultStatus.SUCCESS).setData("队列日志写入成功");
	}
	
	
}
