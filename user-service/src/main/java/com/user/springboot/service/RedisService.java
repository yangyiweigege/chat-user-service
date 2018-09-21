package com.user.springboot.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.domain.UserInfo;

/**
 * redis操作
 * @author yangyiwei
 * @date 2018年6月7日
 * @time 上午9:33:02 随便调教一下
 */
public interface RedisService {

	/**
	 * 测试redis事务
	 * @return
	 */
	public List<Object> execTransaction();
	
	/**
	 * 测试redis管道
	 * @return
	 */
	public String execPipeLined();

	/**
	 * 执行redis hash操作
	 * @param jsonObject
	 * @return
	 */
	public JSONObject hashOperate(JSONObject jsonObject);

	/**
	 * 模拟多线程秒杀
	 * @return
	 */
	public String highKill();

	/**
	 * 模拟分布式锁
	 * @return
	 */
	public String distributeLock();

	/**
	 * redis发布
	 * @param message
	 * @return
	 */
	public boolean publishMessage(String message);

	/**
	 * 将用户信息写入token
	 * @param string
	 * @param userName
	 * @param id
	 * @return
	 */
	public String writeUserInfo(UserInfo userInfo);

	/**
	 * 根据token信息查询用户
	 * @param token
	 * @return
	 */
	public UserInfo getUserInfoByToken(String token);

}
