package com.user.springboot.dao.impl;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.user.springboot.domain.UserInfo;


/**
 * 此接口将自动实现 userInfoDao 并对其进行扩展
 * 
 * @author yangyiwei
 * @date 2018年6月15日
 * @time 上午10:12:27
 */
@Repository
public class UserInfoDaoImpl {
	
	@Resource
	private MongoTemplate mongoTemplate;

	
	public int userNameIsRepeat(String userName) {
		Query query = new Query();
		query.addCriteria(new Criteria().and("user_name").is(userName));
		return (int) mongoTemplate.count(query, UserInfo.class);
	}
	
	public int updateSignById(String userId, String sign) {
		Query query = new Query();
		query.addCriteria(new Criteria().and("_id").is(userId));
		Update update = new Update();
		update.set("sign", sign);
		return mongoTemplate.updateFirst(query, update, UserInfo.class).getN();
	}

	public UserInfo findById(String userId){
		UserInfo userInfo = mongoTemplate.findById(userId,UserInfo.class);
		System.out.println(userInfo);
		return userInfo;
	}
}
