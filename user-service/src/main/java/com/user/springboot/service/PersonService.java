package com.user.springboot.service;
import com.user.springboot.domain.Person;

import java.util.List;

import com.chat.springboot.common.PageBean;
import com.chat.springboot.common.response.Result;

public interface PersonService {

	/**
	 * 插入人员
	 * @param person
	 * @return
	 */
	public Result<Object> insert(Person person);

	/**
	 * 根据id删除
	 * @param id
	 * @return 
	 */
	public Result<Object> delete(Integer id);

	/**
	 * 根据id更新
	 * @param person
	 * @return
	 */
	public Result<Object> update(Person person);

	/**
	 * 查询所有人员
	 * @return
	 */
	public List<Person> findAll();

	/**
	 * 根据id查询对应人员
	 * @param id
	 * @return
	 */
	public Result<Object> findById(Integer id);

	/**
	 * 分页查询数据
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public Result<PageBean<Person>> findByPage(Integer pageSize, Integer pageNo);

}
