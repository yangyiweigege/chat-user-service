package com.user.springboot.service;

import java.util.List;

import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.Result;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.domain.User;
/**
 * <pre>
 * 功       能: 用户业务逻辑层
 * 涉及版本: V3.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2018年3月28日 下午2:55:38
 * Q    Q: 2873824885
 * </pre>
 */
public interface UserService {

	/**
	 * <pre>
	 * 功       能: 保存
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年3月27日 下午7:04:29
	 * Q    Q: 2873824885
	 * </pre>
	 * @throws Exception 
	 */
	public ResponseResult<?> save(User user);

	/**
	 * <pre>
	 * 功       能: 查询全部
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年3月28日 下午12:22:59
	 * Q    Q: 2873824885
	 * </pre>
	 * @param user 
	 * @param currentPage 
	 */
	public List<User> findList(Integer currentPage);

	/**
	 * <pre>
	 * 功       能: 根据属性查找
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年3月28日 下午2:55:28
	 * Q    Q: 2873824885
	 * </pre>
	 */
	public Result<List<User>> findByAttribute(String attribute);

	/**
	 * <pre>
	 * 功       能: 根据id查询
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年5月3日 下午2:46:44
	 * Q    Q: 2873824885
	 * </pre>
	 */
	public User findById(String id);

	/**
	 * <pre>
	 * 功       能: 根据名称访问
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年5月3日 下午3:41:52
	 * Q    Q: 2873824885
	 * </pre>
	 */
	public Result<Object> findByName(String name);
	
	public ResultStatus insertAnotherOne();

	/**
	 * 批量跟新
	 */
    void batchUpdate();
}
