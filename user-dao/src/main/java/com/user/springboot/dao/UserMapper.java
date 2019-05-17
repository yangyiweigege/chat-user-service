package com.user.springboot.dao;

import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.cache.annotation.Cacheable;

import com.chat.springboot.common.MyMapper;
import com.user.springboot.domain.User;

import tk.mybatis.mapper.provider.base.BaseSelectProvider;

import java.util.List;

public interface UserMapper extends MyMapper<User> {

	@Cacheable(value = "user", key = "'users_'+#p0")
	public User findByName(String userName);

	/**
	 * 使用注解缓存
	 */
	@Cacheable(value = "user", key = "'users_'+#p0")
	@SelectProvider(type = BaseSelectProvider.class, method = "dynamicSQL")
	public User selectByPrimaryKey(Object key);


    int batchUpdate(List<User> users);
}