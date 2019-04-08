package com.user.springboot.controller;
import com.chat.springboot.common.annotation.AutowireUser;
import com.chat.springboot.common.annotation.ValidateAttribute;
import com.chat.springboot.common.annotation.ValidateJSON;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.domain.RequestHolder;
import com.user.springboot.domain.UserInfo;
import com.user.springboot.service.RedisService;
import com.user.springboot.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 用户信息控制层
 * 
 * @author yangyiwei
 * @date 2018年7月2日
 * @time 下午4:36:35
 */
@Api(value = "user-info-controller", description = "用户信息控制层")
@RestController
@RequestMapping("/user/info")
public class UserInfoController {

	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private RedisService redisService;

	/**
	 * 用户注册
	 * 
	 * @param userInfo
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/register", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseResult<?> register(@Valid UserInfo userInfo, String birthdayStr, BindingResult bindingResult)
			throws ParseException {
		if (birthdayStr != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			userInfo.setBirthday(format.parse(birthdayStr)); // 设置出生年月
		}
		if (bindingResult.hasErrors()) {
			return new ResponseResult<>(ResultStatus.LACK_PARAM, bindingResult.getFieldError().getDefaultMessage());
		}
		return new ResponseResult<>(userInfoService.register(userInfo));
	}

	/**
	 * 用户登陆
	 * 
	 * @param userInfo
	 * @param bindingResult
	 * @return
	 */
	@ApiOperation(value = "用户登陆")
	@ApiImplicitParam(name = "userInfo", value = "用户信息", required = true, dataType = "UserInfo", paramType = "form")
	@RequestMapping(value = "/login", method = { RequestMethod.POST, RequestMethod.GET })
	@ValidateJSON
	public ResponseResult<?> login(@Valid UserInfo userInfo, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) { // spring-boot自带的校验
			return new ResponseResult<>(ResultStatus.LACK_PARAM, bindingResult.getFieldError().getDefaultMessage());
		}
		ResultStatus resultStatus = userInfoService.login(userInfo);
		if (resultStatus.getCode().intValue() == 0) { // 登陆成功,将token带出去
			String token = redisService.writeUserInfo(userInfo);
			return new ResponseResult<>(ResultStatus.SUCCESS, token);
		}
		return new ResponseResult<>(resultStatus);
	}

	/**
	 * 修改用户签名
	 * 
	 * @return
	 */
	@ApiOperation(value = "修改用户签名")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sign", value = "用户签名", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "int", paramType = "query")
	})
	@RequestMapping(value = "/edit/sign", method = { RequestMethod.POST, RequestMethod.GET })
	@ValidateAttribute(attributes = { "sign" })
	@AutowireUser("token")
	public ResponseResult<?> editSign(String sign) {
		return new ResponseResult<>(userInfoService.updateSignById(RequestHolder.USER_INFO.get().getId(), sign));
	}

	/**
	 * 获取单个用户信息
	 * 
	 * @return
	 */
	@ApiOperation(value = "根据用户id获取获取信息")
	@ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "String", paramType = "query")
	@RequestMapping(value = "/load/one", method = { RequestMethod.GET })
	@AutowireUser("token")
	public ResponseResult<UserInfo> loadOne() {
		return new ResponseResult<UserInfo>(ResultStatus.SUCCESS, userInfoService.loadUserById(RequestHolder.USER_INFO.get().getId()));
	}

}
