package com.user.springboot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.user.springboot.interceptor.AutowireUserInterceptor;
import com.user.springboot.interceptor.UserLoginInterceptor;
import com.user.springboot.interceptor.ValidateAttributeInterceptor;
import com.user.springboot.interceptor.ValidatePageInterceptor;




/**
 * <pre>
 * 功       能: spring-boot 拦截器
 * 涉及版本: V3.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2018年4月2日 下午9:22:52
 * Q    Q: 2873824885
 * </pre>
 */
@Configuration
public class WebMvcInterceptor extends WebMvcConfigurerAdapter {

	@Autowired
	private AutowireUserInterceptor autowireUserInterceptor;
	
	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//registry.addInterceptor(new OneInterceptor()).addPathPatterns("/**");
		registry.addInterceptor(new UserLoginInterceptor()).addPathPatterns("/**");//注册session校验
		registry.addInterceptor(new ValidateAttributeInterceptor()).addPathPatterns("/**");//注册属性校验
		registry.addInterceptor(new ValidatePageInterceptor()).addPathPatterns("/**"); //注册分页
		registry.addInterceptor(autowireUserInterceptor).addPathPatterns("/**");//本地线程注入用户信息
		super.addInterceptors(registry);//注册该拦截器
	}
	
}
