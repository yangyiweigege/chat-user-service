package com.user.springboot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chat.springboot.common.PageBean;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.dao.PersonDao;
import com.user.springboot.domain.Person;
import com.chat.springboot.common.response.Result;

import com.user.springboot.service.PersonService;

@Service
public class PersonServiceBean implements PersonService {

	@Resource
	private PersonDao personDao;
	

	@Override
	public Result<Object> insert(Person person) {
		Result<Object> result = new Result<Object>();
		personDao.insert(person);
		return result.setCode(ResultStatus.SUCCESS).setData(person);
	}

	@Override
	public Result<Object> delete(Integer id) {
		Result<Object> result = new Result<Object>();
		if (id == null) {
			return result.setCode(ResultStatus.LACK_PARAM).setData("id");
		}
		personDao.delete(id);
		return result.setCode(ResultStatus.SUCCESS);
	}

	@Override
	public Result<Object> update(Person person) {
		Result<Object> result = new Result<Object>();
		personDao.updateExistDataById(person);
		return result.setCode(ResultStatus.SUCCESS);
	}

	@Override
	public Result<Object> findAll() {
		Result<Object> result = new Result<Object>();
		List<Person> list = personDao.findAll();
		return result.setCode(ResultStatus.SUCCESS).setData(list);
	}

	@Override
	public Result<Object> findById(Integer id) {
		Result<Object> result = new Result<Object>();
		Person person = personDao.findOne(id);
		return result.setCode(ResultStatus.SUCCESS).setData(person);
	}

	@Override
	public Result<PageBean<Person>> findByPage(Integer pageSize, Integer pageNo) {
		Result<PageBean<Person>> result = new Result<PageBean<Person>>();
		PageBean<Person> pageBean = new PageBean<Person>();//
		pageBean.setCurrentPage(pageNo);
		pageBean.setPageSize(pageSize);
		pageBean.setTotalCount((int) personDao.count());//
		List<Person> list = personDao.findPage(pageSize, pageNo); 
		pageBean.setList(list);
		return result.setCode(ResultStatus.SUCCESS).setData(pageBean);
	}

}
