package com.user.springboot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.dao.UserFriendDao;
import com.user.springboot.dao.UserInfoDao;
import com.user.springboot.domain.UserFriend;
import com.user.springboot.domain.UserInfo;
import com.user.springboot.service.UserFriendService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class UserFriendBean implements UserFriendService {

	@Resource
	private UserFriendDao userFriendDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private JedisPool jedisPool;

	@Override
	public ResultStatus addFriend(String userId, String friendId) {
		if (userId.equals(friendId)) { // 不可添加自身
			return ResultStatus.CAN_NOT_ADD_SELF;
		}
		UserInfo userInfo = userInfoDao.findOne(friendId);
		if (userInfo == null) { // 好友不存在
			return ResultStatus.DATA_NOT_FIND;
		}
		if (userFriendDao.isRepeatAddFriend(userId, friendId) > 0) { // 不可重复添加好友
			return ResultStatus.CAN_NOT_ADD_REPEAT_FRIEND;
		}
		userInfo.setPassword(null);
		userInfo.setSalt(null);
		int count = userFriendDao.addFriend(userId, userInfo);
		if (count < 1) {
			return ResultStatus.UPDATE_FAIL;
		}
		return ResultStatus.SUCCESS;
	}

	@Override
	public List<UserInfo> getFriendListByUid(String userId) {
		// 根据userId 查询到匹配记录
		UserFriend userFriend = userFriendDao.findByUserId(userId);
		// 获取该用户所有好友
		List<UserInfo> friends = userFriend.getFriends();
		String[] friendsIds = new String[friends.size()];
		for (int i = 0; i < friends.size(); i++) { // 遍 历好友列表 获取所有好友id
			friendsIds[i] = friends.get(i).getId();
		}
		// 此处去redis查询 所有好友在线状态 并设置
		if (friendsIds.length > 0) {
			Jedis jedis = jedisPool.getResource();
			List<String> friendStatus = jedis.hmget("login_online_user", friendsIds);
			for (int i = 0; i < friendStatus.size(); i++) {
				if (friendStatus.get(i) != null) { // 说明该好友登陆过，状态在线
					friends.get(i).setOnline(true);
				} else {
					friends.get(i).setOnline(false);
				}
			}
			jedis.disconnect();
		}
		return friends;
	}

	@Override
	public ResultStatus removeFriend(String id, String friendId) {
		// 删除好友
		int count = userFriendDao.FriendRemove(id, friendId);
		if (count < 1) {
			return ResultStatus.UPDATE_FAIL;
		}
		return ResultStatus.SUCCESS;
	}

}
