package com.user.springboot.dao;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.springboot.domain.UserInfo;


/**
 * 用户信息数据层
 * 
 * @author yangyiwei
 * @date 2018年7月2日
 * @time 下午4:46:13
 */
public interface UserInfoDao extends MongoRepository<UserInfo, String> {
	
	/**
	 * 查看该用户是否存在
	 * @param userName
	 * @return
	 */
	public int userNameIsRepeat(String userName);
	
	/**
	 * 根据用户名查询用户信息
	 * @param userName
	 * @return
	 */
	public UserInfo findByUserName(String userName);

	/**
	 * 更新用户签名
	 * @param userName
	 * @param sign
	 * @return
	 */
	@CacheEvict(value="userInfo", key="'userInfo_'+#p0") 
	public int updateSignById(String userId, String sign);

	/**
	 * 获取单个用户信息
	 * @param userId
	 * @return
	 */
	@Cacheable(value="userInfo", key="'userInfo_'+#p0")
    public UserInfo findById(String userId);
}
