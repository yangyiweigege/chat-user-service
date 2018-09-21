package com.user.springboot.domain;

import java.util.Date;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection = "user_info")
public class UserInfo {

	/**
	 * 用户id标志
	 */
	@Id
	@JsonInclude(Include.NON_NULL)
	private String id;

	/**
	 * 用户名
	 */
	@NotNull(message = "用户名不能为空")
	@Field("user_name")
	@JsonInclude(Include.NON_NULL)
	private String userName;

	/**
	 * 用户密码
	 */
	@NotNull(message = "密码不能为空")
	@JsonInclude(Include.NON_NULL)
	private String password;

	/**
	 * 密码盐 加密使用
	 */
	@JsonInclude(Include.NON_NULL)
	private String salt;

	/**
	 * 用户性别
	 */
	@JsonInclude(Include.NON_NULL)
	private Integer sex;

	/**
	 * 用户生日
	 */
	@JsonInclude(Include.NON_NULL)
	private Date birthday;

	/**
	 * 用户签名
	 */
	@JsonInclude(Include.NON_NULL)
	private String sign;

	/**
	 * 用户状态是否在线
	 */
	@JsonInclude(Include.NON_NULL)
	private boolean isOnline;

	public UserInfo() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", userName=" + userName + ", password=" + password + ", salt=" + salt + ", sex="
				+ sex + ", birthday=" + birthday + ", sign=" + sign + ", isOnline=" + isOnline + "]";
	}

}
