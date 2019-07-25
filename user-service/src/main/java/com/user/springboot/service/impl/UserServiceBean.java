package com.user.springboot.service.impl;

import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.Result;
import com.chat.springboot.common.response.ResultStatus;
import com.github.pagehelper.PageHelper;
import com.user.springboot.dao.UserMapper;
import com.user.springboot.domain.User;
import com.user.springboot.service.PersonService;
import com.user.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
//@Transactional
public class UserServiceBean implements UserService {
	@Resource
	private UserMapper userMapper;
	@Value("${server.port}")
	private String port;
	@Autowired
	private PersonService personService;

	

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation =  Isolation.READ_COMMITTED)
	public ResponseResult save(User user) {
		userMapper.insert(user);
		log.info("执行到这里....");
		int i = 1 / 0;
		/*System.out.println("当前线程id:" + Thread.currentThread().getName());
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
				System.out.println("事物执行 当前线程id:" + Thread.currentThread().getName());
				System.out.println("事物执行完毕...");
				int i = 1 / 0;
			}

		});*/
		//userServiceBiz.insertAnotherOne();
		//this.insertAnotherOne();// 测试事务
		return new ResponseResult();
	}

	@Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
	public ResultStatus insertAnotherOne() {
		User user = new User();
		user.setUserName("测试B事务的一条数据");
		user.setPhone("13150583605");
		user.setPassword("13150583605");
		userMapper.insert(user);
		
		int i = 1 / 0;
		return ResultStatus.SUCCESS;
	}

	@Override
	public void batchUpdate() {
		List<User> users = new ArrayList<>();
		User user = new User();
		user.setUserId(1);
		user.setUserName("测试B事务的一条数据");
		user.setPhone("1315051283605");
		user.setPassword("1315058123605");
		User user2 = new User();
		user2.setUserId(2);
		user2.setUserName("测试B事务的一条数据");
		user2.setPhone("13150581213605");
		user2.setPassword("13150121583605");
		users.add(user);
		users.add(user2);
		System.out.println("受影响行数:" + userMapper.batchUpdate(users));
	}

	@Override
	//@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, isolation = Isolation.DEFAULT)
	public List<User> findList(Integer currentPage) {
		PageHelper.startPage(currentPage, 2, false);
		List<User> list = userMapper.selectAll();
		return list;
	}

	// 事务 support 支持当前事务，如果没有，则以非事务执行 required:支持当前事务，如果当前没有事务，就新建一个事务 默认
	@Override
	//@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, isolation = Isolation.DEFAULT)
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
