package com.user.springboot.domain;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;




@Entity(name = "girl")
@Component
public class Girl {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@JsonInclude(Include.NON_NULL)
	private String name;
	
	@JsonFormat(pattern = "yyyy-MM-DD", locale = "zh", timezone = "GMT+8")
	private java.util.Date date = new java.util.Date();
	
	@Min(value = 18, message = "岁数不得小于18岁")
	@JsonInclude(Include.NON_NULL)
	private Integer age;
	
	public Girl() {
	
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
	
	

	public java.util.Date getDate() {
		return this.date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Girl [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
	
	

}
