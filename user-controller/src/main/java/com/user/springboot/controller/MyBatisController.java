package com.user.springboot.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.annotation.CheckPageBean;
import com.chat.springboot.common.annotation.ValidateAttribute;
import com.chat.springboot.common.excel.ExcelUtil;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.Result;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.domain.User;
import com.user.springboot.service.UserService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/mybatis")
@Api(value = "mybatis-controller", description = "mybatis控制层")
@Slf4j
public class MyBatisController {

	@Autowired
	private UserService userService;
	@Value("${server.port}")
	private String port;
	
	@RequestMapping(value = "/generate")
	public ResponseResult<?> generate() {
		String string = "{\"name\":\"yangyiwei\"}";
		return new ResponseResult<String>(ResultStatus.SUCCESS, string);
	}

	@RequestMapping(value = "/json")
	public ResponseResult<?> getJSON(@RequestBody JSONObject jsonObject) {
		log.info("jsonObject请求......");
		jsonObject.put("port", port);
		return new ResponseResult<JSONObject>(ResultStatus.SUCCESS, jsonObject);
	}
	
	/**
	 * 测试嵌套事务 比如serviceA中 调用a方法 a方法继续调用b方法 看能否回滚 的确可以回滚
	 * @return
	 */
	@RequestMapping(value = "/transaction", method = {RequestMethod.GET, RequestMethod.POST})
	@ValidateAttribute(attributes = {"userName"})
	public ResponseResult<?> testInnerTransaction(User user) {
		System.out.println("业务层：" + userService.getClass().getName());
		return new ResponseResult<JSONObject>(userService.save(user));
	}

	@RequestMapping(value = "/save", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseResult<?> saveUser(User user) throws Exception {
		return new ResponseResult<>(userService.save(user));
	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	@CheckPageBean(currentPage = "currentPage")
	public Result<List<User>> userList(String currentPage) {
		List<User> list = userService.findList(Integer.parseInt(currentPage));
		return new Result<List<User>>(ResultStatus.SUCCESS, list);
	}

	@RequestMapping(value = "/list/export/{currentPage}", method = { RequestMethod.GET, RequestMethod.POST })
	public void exportUserList(@PathVariable("currentPage") Integer currentPage, HttpServletRequest request,
			HttpServletResponse response) {
		List<User> list = userService.findList(currentPage);
		ExcelUtil.exportExcel(list, "出错记录", "出错记录", User.class, "出错记录.xls", response, request);
	}

	@RequestMapping(value = "/find/attribute", method = { RequestMethod.GET, RequestMethod.POST })
	public Result<List<User>> findByAttribute(String user) {
		return userService.findByAttribute(user);
	}

	/**
	 * <pre>
	 * 功       能: 使用了缓存
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年5月3日 下午3:19:11
	 * Q    Q: 2873824885
	 * </pre>
	 */
	@RequestMapping(value = "/find/detail/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public Result<Object> findById(@PathVariable("id") String id) {
		Result<Object> result = new Result<Object>();
		return result.setCode(ResultStatus.SUCCESS).setData(userService.findById(id));
	}

	@RequestMapping(value = "/find/name/{name}", method = { RequestMethod.GET, RequestMethod.POST })
	public Result<Object> findByName(User user, @PathVariable("name") String name) {
		Result<Object> result = userService.findByName(name);
		return result;
	}
	
	@RequestMapping(value = "/insertAnother", method = { RequestMethod.GET, RequestMethod.POST })
	public Result<Object> finsertAnother() {
		return new Result<>(userService.insertAnotherOne());
	}

}
