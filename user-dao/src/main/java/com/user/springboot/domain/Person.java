package com.user.springboot.domain;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 人员实体类--mongodb
 * @author yangyiwei
 * @date 2018年6月4日
 * @time 下午1:59:36
 */
@Document(collection = "person")
public class Person {
	
	/**
	 * id标识
	 */
	@Id
	@NotNull(message = "id标识不能为空")
	private Integer id;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 年龄
	 */
	private Integer age;
	
	
	public Person() {
		
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Integer getAge() {
		return age;
	}


	public void setAge(Integer age) {
		this.age = age;
	}


	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
	
	

}
