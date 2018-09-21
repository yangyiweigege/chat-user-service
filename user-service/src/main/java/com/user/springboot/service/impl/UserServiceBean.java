package com.user.springboot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.chat.springboot.common.response.Result;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.dao.UserMapper;
import com.user.springboot.domain.User;
import com.user.springboot.service.UserService;
import com.github.pagehelper.PageHelper;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
@Transactional
public class UserServiceBean implements UserService{
	@Resource
	private UserMapper userMapper;
	@Value("${server.port}")
	private String port;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Result<Object> save(User user) throws Exception{
		Result<Object> result = new Result<Object>();
		userMapper.insert(user);
		result.setCode(ResultStatus.SUCCESS).setData(user);
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, isolation = Isolation.DEFAULT)
	public List<User> findList(Integer currentPage) {
		PageHelper.startPage(currentPage, 2, false);
		List<User> list = userMapper.selectAll();
		return list;
	}

	//事务 support 支持当前事务，如果没有，则以非事务执行 required:支持当前事务，如果当前没有事务，就新建一个事务 默认
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, isolation = Isolation.DEFAULT)
	public Result<List<User>> findByAttribute(String attribute) {
		Result<List<User>> result = new Result<List<User>>();
		Example example = new Example(User.class);
		Criteria criteria = example.createCriteria();
		criteria.andLike("userName", "%" + attribute + "%");
		List<User> list = userMapper.selectByExample(example);
		return result.setCode(ResultStatus.SUCCESS).setData(list);
	}

	@Override
	public User findById(String id) {
		return userMapper.selectByPrimaryKey(Integer.parseInt(id));
	}

	@Override
	public Result<Object> findByName(String name) {
		Result<Object> result = new Result<Object>();
		User user = userMapper.findByName(name);
		return result.setCode(ResultStatus.SUCCESS).setData(user);
	}

}
