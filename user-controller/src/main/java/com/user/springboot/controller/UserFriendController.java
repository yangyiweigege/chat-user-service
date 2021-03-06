package com.user.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chat.springboot.common.annotation.AutowireUser;
import com.chat.springboot.common.annotation.ValidateAttribute;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.domain.RequestHolder;
import com.user.springboot.domain.UserInfo;
import com.user.springboot.service.UserFriendService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 用户好友控制层
 * 
 * @author yangyiwei
 * @date 2018年7月5日
 * @time 下午3:36:04
 */
@Api(value = "user-friend-controoler", description = "用户好友控制层")
@RestController
@RequestMapping("/user/friend")
public class UserFriendController {

	@Autowired
	private UserFriendService userFriendService;


	@ApiOperation(value = "用户添加好友")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "friendId", value = "要添加的好友id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户令牌", required = false, dataType = "String", paramType = "query") })
	@RequestMapping(value = "/add", method = { RequestMethod.GET })
	@ValidateAttribute(attributes = { "token", "friendId" })
	@AutowireUser("token")
	public ResponseResult<?> addFirend(String friendId) {
		return new ResponseResult<>(userFriendService.addFriend(RequestHolder.USER_INFO.get().getId(), friendId));
	}

	@ApiOperation(value = "用户删除好友")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "friendId", value = "要删除的好友id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户令牌", required = false, dataType = "String", paramType = "query") })
	@RequestMapping(value = "/remove", method = { RequestMethod.GET })
	@ValidateAttribute(attributes = { "token", "friendId" })
	@AutowireUser("token")
	public ResponseResult<?> removeFirend(String friendId) {
		return new ResponseResult<>(userFriendService.removeFriend(RequestHolder.USER_INFO.get().getId(), friendId));
	}

	@ApiOperation(value = "查询用户好友列表")
	@ApiImplicitParam(name = "token", value = "用户令牌", required = false, dataType = "String", paramType = "query")
	@RequestMapping(value = "/list", method = { RequestMethod.POST, RequestMethod.GET })
	@AutowireUser("token")
	public ResponseResult<List<UserInfo>> getFriendListByUid() {
		List<UserInfo> userInfos = userFriendService.getFriendListByUid(RequestHolder.USER_INFO.get().getId());
		return new ResponseResult<>(ResultStatus.SUCCESS, userInfos);
	}

}
