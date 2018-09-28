package com.user.springboot.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.request.HttpRequestUtil;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;

import io.swagger.annotations.Api;

/**
 * <pre>
 * 功       能: 远程调用别的服务
 * 涉及版本: V3.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2018年4月9日 下午1:13:23
 * Q    Q: 2873824885
 * </pre>
 */
@Api(value = "rest-request-controoler", description = "远程调用控制层")
@RestController
@RequestMapping("/rest/request")
public class RestRequestController {

	@Autowired
	private RestTemplate restTemplate;// 服务调用
	//@Autowired
	private HttpRequestUtil httpRequest;

	/**
	 * <pre>
	 * 功       能: 传递参数为json形式
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年4月9日 下午2:39:51
	 * Q    Q: 2873824885
	 * </pre>
	 */
	@RequestMapping(value = "/json", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseResult<Object> invokeAnotherWeb() {
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		JSONObject param = new JSONObject();
		param.put("type", 1);
		HttpEntity<String> formEntity = new HttpEntity<String>(param.toString(), headers);
		JSONObject result = restTemplate.postForObject("http://192.168.10.158:8089/datacenter/model/findAll.action",
				formEntity, JSONObject.class);
		return new ResponseResult<>(ResultStatus.SUCCESS, result);
	}

	/**
	 * <pre>
	 * 功       能: 传递参数为json形式
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年4月9日 下午2:39:51
	 * Q    Q: 2873824885
	 * </pre>
	 */
	@RequestMapping(value = "/util", method = { RequestMethod.POST, RequestMethod.GET })
	public JSONObject httpRequestTest() {
		JSONObject param = new JSONObject();
		param.put("type", 1);
		JSONObject result = httpRequest.sendJSONRequest("http://192.168.10.158:8089/datacenter/model/findAll.action",
				param);
		return result;
	}

	/**
	 * <pre>
	 * 功       能: get请求
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年4月9日 下午2:39:51
	 * Q    Q: 2873824885
	 * </pre>
	 */
	@RequestMapping(value = "/get", method = { RequestMethod.POST, RequestMethod.GET })
	public JSONObject getRequest() {
		JSONObject result = restTemplate.getForObject("http://localhost:8000/rest/hello", JSONObject.class);
		return result;
	}

	/**
	 * <pre>
	 * 功       能: post请求
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年4月9日 下午2:39:51
	 * Q    Q: 2873824885
	 * </pre>
	 */
	@RequestMapping(value = "/post", method = { RequestMethod.POST, RequestMethod.GET })
	public String postRequest() {
		Map<String, String> param = new HashMap<>();
		param.put("", "");
		String result = httpRequest.sendPostRequest("", param);
		return result;
	}

}
