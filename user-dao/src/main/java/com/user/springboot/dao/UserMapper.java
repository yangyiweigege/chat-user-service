package com.user.springboot.dao;

import org.springframework.cache.annotation.Cacheable;

import com.chat.springboot.common.MyMapper;
import com.user.springboot.domain.User;

public interface UserMapper extends MyMapper<User> {
	
	//@Cacheable(value = "user")
	public User findByName(String userName);
	
	/**
	 * 使用注解缓存
	 */
	@Cacheable(value="user", key="'users_'+#p0")
	public User selectByPrimaryKey(Object key);
}