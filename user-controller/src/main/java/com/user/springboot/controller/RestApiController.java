package com.user.springboot.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;
import com.sun.tools.internal.ws.wsdl.document.jaxws.Exception;

import io.swagger.annotations.Api;

/**
 * 提供各种接口 让其余服务调用
 * @author yangyiwei
 * @date 2018年10月15日
 * @time 下午3:48:20
 */
@RestController
@RequestMapping("/api")
@Api(value = "rest-controller", description ="提供外部接口测试")
public class RestApiController {
	
	@RequestMapping(value = "/get", method = {RequestMethod.GET})
	public ResponseResult<?> getRequest(String id) {
		return new ResponseResult<>(ResultStatus.SUCCESS, "获取到的id是：" + id);
	}
	
	@RequestMapping(value = "/post", method = {RequestMethod.POST, RequestMethod.GET})
	public ResponseResult<?> postRequest(String id) {
		//throw new RuntimeException("抛出异常了大哥...");
		int i = 1 / 0;
		return new ResponseResult<>(ResultStatus.SUCCESS, "获取到的id是：" + id);
	}
	
	@RequestMapping(value = "/json")
	public ResponseResult<?> postRequest(@RequestBody JSONObject jsonObject) {
		return new ResponseResult<>(ResultStatus.SUCCESS, jsonObject);
	}

}
