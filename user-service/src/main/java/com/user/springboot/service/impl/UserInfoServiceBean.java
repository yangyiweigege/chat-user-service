package com.user.springboot.service.impl;
import java.util.UUID;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.chat.springboot.common.SignUtil;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.dao.UserFriendDao;
import com.user.springboot.dao.UserInfoDao;
import com.user.springboot.domain.UserFriend;
import com.user.springboot.domain.UserInfo;
import com.user.springboot.service.UserInfoService;
import com.chat.springboot.common.response.ProjectException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class UserInfoServiceBean implements UserInfoService {

	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private UserFriendDao userFriendDao;
	@Resource
	private JedisPool jedisPool;

	@Override
	public ResultStatus register(UserInfo userInfo) {
		int count = userInfoDao.userNameIsRepeat(userInfo.getUserName());
		if (count > 0) {
			return ResultStatus.USER_IS_REGISTER;
		}
		// 通过shiro运算，获取盐和加密后的值
		String[] result = SignUtil.AddSalt(userInfo.getUserName(), userInfo.getPassword(), null);
		userInfo.setSalt(result[1]);
		userInfo.setPassword(result[0]);

		// System.out.println("密码盐" + userInfo.getSalt() + "密码：" +
		// userInfo.getPassword());

		/* 由于mongodb自身不带事务。所以多表 需要手动实现，实际操作中应当尽量避免 */
		userInfoDao.insert(userInfo); // 写入用户信息表
		UserFriend userFriend = new UserFriend();
		userFriend.setId(UUID.randomUUID().toString());
		userFriend.setUserName(userInfo.getUserName());
		userFriend.setUserId(userInfo.getId());
		try {
			userFriendDao.insert(userFriend);
		} catch (Exception e) {
			userInfoDao.delete(userInfo.getId());// 如果插入好友表失败，则删除之前信息表插入结果
			throw new ProjectException(ResultStatus.TRANSACTION_FAIL);
		}
		return ResultStatus.SUCCESS;

	}

	@Override
	public ResultStatus login(UserInfo userInfo) {
		UserInfo searchUser = userInfoDao.findByUserName(userInfo.getUserName());
		if (searchUser == null) { // 未找到该用户
			return ResultStatus.USER_NOT_EXIST;
		}
		String password = SignUtil.AddSalt(userInfo.getUserName(), userInfo.getPassword(), searchUser.getSalt())[0]; // 根据用过户传输的密码以及查询出来的salt
																														// 计算值
		if (password.equals(searchUser.getPassword())) { // 加密后结果 与 预期一致
			userInfo.setId(searchUser.getId());
			//redis缓存中 存入用户在线状态()
			Jedis jedis = jedisPool.getResource();
			jedis.hset("login_online_user", searchUser.getId(), "yes");
			jedis.disconnect();
			return ResultStatus.SUCCESS;
		}
		return ResultStatus.LOGIN_FAIL;
	}

	@Override
	public ResultStatus updateSignById(String userId, String sign) {
		int num = userInfoDao.updateSignById(userId, sign);
		if (num < 1) {
			return ResultStatus.UPDATE_FAIL;
		}
		return ResultStatus.SUCCESS;
	}

	@Override
	public UserInfo loadUserById(String userId) {
		return userInfoDao.findById(userId);
	}

}
