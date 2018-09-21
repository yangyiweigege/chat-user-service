package com.user.springboot.service;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.domain.UserInfo;


/**
 * 用户信息业务逻辑层
 * @author yangyiwei
 * @date 2018年7月2日
 * @time 下午4:39:59
 */
public interface UserInfoService {

	/**
	 * 用户信息注册
	 * @param userInfo 用户
	 * @return 
	 */
	public ResultStatus register(UserInfo userInfo);

	/**
	 * 用户登陆
	 * @param userInfo
	 * @return
	 */
	public ResultStatus login(UserInfo userInfo);

	/**
	 * 修改用户签名
	 * @param userName
	 * @param sign
	 */
	public ResultStatus updateSignById(String userId, String sign);

	/**
	 * 根据用户id查询用户完成信息
	 * @param userId
	 * @return
	 */
    public UserInfo loadUserById(String userId);
}
