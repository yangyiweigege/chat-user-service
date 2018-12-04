package com.user.springboot.domain;

/**
 * 可以用来传递用户信息
 * 
 * @author yangyiwei
 * @date 2018年11月15日
 * @time 下午7:24:15
 */
public class RequestHolder {

	// 用来保存用户信息
	public final static ThreadLocal<UserInfo> USER_INFO = new ThreadLocal<>();
	// 用来保存当前用户token信息
	public final static ThreadLocal<String> TOKEN = new ThreadLocal<>();

}
