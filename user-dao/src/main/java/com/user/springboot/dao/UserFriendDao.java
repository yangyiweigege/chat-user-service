package com.user.springboot.dao;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.springboot.domain.UserFriend;
import com.user.springboot.domain.UserInfo;

/**
 * 用户好友数据层
 * @author yangyiwei
 * @date 2018年7月5日
 * @time 下午4:09:44
 */
public interface UserFriendDao extends MongoRepository<UserFriend, String> {
	
	/**
	 * 添加好友
	 * @param userId
	 * @param userInfo
	 * @return
	 */
	public int addFriend(String userId, UserInfo userInfo);

	/**
	 * 根据用户id查询用户好友列表
	 * @param userId
	 * @return
	 */
	public UserFriend findByUserId(String userId);

	/**
	 * 判断是否重复添加好友
	 * @param userId
	 * @param friendId
	 * @return
	 */
	public int isRepeatAddFriend(String userId, String friendId);

	/**
	 * 删除好友
	 * @param id
	 * @param friendId
	 * @return
	 */
	public int FriendRemove(String id, String friendId);

}
