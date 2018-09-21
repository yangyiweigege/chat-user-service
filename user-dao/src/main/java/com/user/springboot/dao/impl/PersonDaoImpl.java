package com.user.springboot.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.user.springboot.domain.Person;

/**
 * 此接口将自动实现 personDao 并对其进行扩展
 * 
 * @author yangyiwei
 * @date 2018年6月15日
 * @time 上午10:12:27
 */
@Repository
public class PersonDaoImpl {

	@Autowired
	private MongoTemplate mongoTemplate;

	public int updateExistDataById(Person person) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.and("_id").is(person.getId());
		query.addCriteria(criteria);
		Update update = new Update();
		if (person.getName() != null) {
			update.set("name", person.getName());
		}
		if (person.getAge() != null) {
			update.set("age", person.getAge());
		}
		return mongoTemplate.updateFirst(query, update, Person.class).getN();
	}

	public List<Person> findPage(Integer pageSize, Integer pageNo) {
		Query query = new Query();
		query.skip((pageNo - 1) * pageSize).limit(pageSize);
		return mongoTemplate.find(query, Person.class);
	}
}
