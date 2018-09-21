package com.user.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * <pre>
 * 功       能: 测试配置好的redis服务
 * 涉及版本: V3.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2018年5月14日 上午10:48:57
 * Q    Q: 2873824885
 * </pre>
 */
@Api(value = "redis-controller", description = " 测试配置好的redis服务")
@RestController
@RequestMapping("/redis")
public class RedisController {

	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	private RedisService redisService;

	@GetMapping("/template")
	public ResponseResult<?> test() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", "杨乙伟");
		jsonObject.put("age", 26);
		redisTemplate.opsForValue().set("redis-test", "spring-boot");
		redisTemplate.opsForValue().set("information", jsonObject.toJSONString());
		return new ResponseResult<String>(ResultStatus.SUCCESS, redisTemplate.opsForValue().get("information"));
	}

	/**
	 * 执行redis事务
	 * 
	 * @return
	 */
	@GetMapping("/transaction")
	public ResponseResult<Object> jedisTest() {
		return new ResponseResult<>(ResultStatus.SUCCESS, redisService.execTransaction());
	}

	/**
	 * 测试redis管道
	 * 
	 * @return
	 */
	@RequestMapping(value = "/pipe", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseResult<Object> redisPipelined() {
		return new ResponseResult<>(ResultStatus.SUCCESS, redisService.execPipeLined());
	}

	/**
	 * redis hash操作设置
	 */
	@ApiOperation(value = "将对象写入redis")
	@ApiImplicitParam(name = "jsonObject", value = "json对象", required = true, dataType = "Object")
	@RequestMapping(value = "/hash", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseResult<Object> haseOperate(@RequestBody JSONObject jsonObject) {
		System.out.println(jsonObject.getClass().getName());
		System.out.println(jsonObject.toString());
		return new ResponseResult<Object>(ResultStatus.SUCCESS, redisService.hashOperate(jsonObject));
	}

	/**
	 * redis模拟多线程并发访问，防止超卖
	 */
	@ApiOperation(value = "将对象写入redis")
	@ApiImplicitParam(name = "jsonObject", value = "json对象", required = true, dataType = "Object")
	@RequestMapping(value = "/second/kill", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseResult<Object> secondKill(@RequestBody JSONObject jsonObject) {
		return new ResponseResult<>(ResultStatus.SUCCESS, redisService.highKill());
	}

	/**
	 * 模拟实现分布式锁
	 * 
	 * @return
	 */
	@ApiOperation(value = "模拟实现分布式锁")
	@RequestMapping(value = "/distribute/lock", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseResult<Object> distributeLock() {
		return new ResponseResult<>(ResultStatus.SUCCESS, redisService.distributeLock());
	}

	@ApiOperation("redis发布消息")
	@ApiImplicitParam(name = "jsonObject", value = "json对象", required = true, dataType = "Object", paramType = "query")
	public ResponseResult<String> publishMessage(String message) {
		redisService.publishMessage(message);
		return new ResponseResult<String>(ResultStatus.SUCCESS, message);
	}
}
