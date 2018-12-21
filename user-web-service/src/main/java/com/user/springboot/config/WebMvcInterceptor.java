package com.user.springboot.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.user.springboot.interceptor.AutowireUserInterceptor;
import com.user.springboot.interceptor.UserLoginInterceptor;
import com.user.springboot.interceptor.ValidateAttributeInterceptor;
import com.user.springboot.interceptor.ValidateJSONInterceptor;
import com.user.springboot.interceptor.ValidatePageInterceptor;




/**
 * springboot拦截器注册配置
 * @author yangyiwei
 * @date 2018年11月20日
 * @time 上午11:00:50
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
		//registry.addInterceptor(new UserLoginInterceptor()).addPathPatterns("/**");//注册session校验
		registry.addInterceptor(new ValidateAttributeInterceptor()).addPathPatterns("/**");//注册属性校验
		//registry.addInterceptor(new ValidateJSONInterceptor()).addPathPatterns("/**");//注册json数据校验拦截器
		//registry.addInterceptor(new ValidatePageInterceptor()).addPathPatterns("/**"); //注册分页信息添加
		registry.addInterceptor(autowireUserInterceptor).addPathPatterns("/**");//本地线程注入用户信息
		super.addInterceptors(registry);//注册该拦截器
	}
	
}
