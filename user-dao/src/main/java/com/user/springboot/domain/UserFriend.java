package com.user.springboot.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 用户好友实体类
 * @author yangyiwei
 * @date 2018年7月5日
 * @time 下午3:41:40
 */
@Document(collection = "user_friend")
public class UserFriend {
	
	/**
	 * id
	 */
	@Id
	private String id;
	
	/**
	 * 用户id
	 */ 
	@Field("user_id")
	private String userId;
	
	/**
	 * 用户名
	 */
	@Field("user_name")
	private String userName;
	
	/**
	 * 用户好友
	 */
	@Field("friends")
	private List<UserInfo> friends = new ArrayList<UserInfo>();
	
	public UserFriend() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<UserInfo> getFriends() {
		return friends;
	}

	public void setFriends(List<UserInfo> friends) {
		this.friends = friends;
	}

	@Override
	public String toString() {
		return "UserFriend [id=" + id + ", userId=" + userId + ", userName=" + userName + ", friends=" + friends + "]";
	}
	
	

}
