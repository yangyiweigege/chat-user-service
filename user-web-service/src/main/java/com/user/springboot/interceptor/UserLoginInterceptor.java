package com.user.springboot.interceptor;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.annotation.ValidateSession;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;



/**
 * 用户登陆拦截器
 * 
 * @author yangyiwei
 * @date 2018年7月3日
 * @time 下午5:00:14
 */
public class UserLoginInterceptor implements HandlerInterceptor {

	private final static Logger logger = Logger.getLogger(UserLoginInterceptor.class);
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("执行session校验.......");
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			ValidateSession validateSession = method.getAnnotation(ValidateSession.class);
			if (validateSession == null) { // 不需要校验权限 直接放行
				return true;
			}
			if (!validateSession.value()) {
				return true;
			}
			logger.info("ip地址:" + request.getRemoteAddr() + "...访问的url:" + request.getRequestURI() + "正在判定是否登陆过....");
			// 校验是否存在session
			if (request.getSession().getAttribute("userName") == null || request.getSession().getAttribute("userId") == null) {
				PrintWriter out = null;
				try {
					ResponseResult<Object> resultUtil = new ResponseResult<Object>();
					resultUtil.setCode(ResultStatus.USER_IS_NULL).setData("请登录后再试！");
					response.setContentType("application/json;charset=utf-8");
					out = response.getWriter();
					out.write(JSONObject.toJSONString(resultUtil));
					out.flush();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
					out.write("服务器异常!");
				}
				return false;
			}
			return true; // 存在则放行
		}
		return true;
	}

}
