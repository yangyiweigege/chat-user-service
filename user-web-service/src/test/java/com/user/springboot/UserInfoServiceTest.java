package com.user.springboot;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.user.springboot.domain.UserInfo;
import com.user.springboot.service.UserInfoService;

public class UserInfoServiceTest extends SpringBootChatApplicationTests{
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Test
	public void findUser() {
		UserInfo userInfo = userInfoService.loadUserById("123");
		System.out.println("我实行执行得结果:" + userInfo);
	}

}
