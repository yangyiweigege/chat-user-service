package com.user.springboot.interceptor;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.annotation.AutowireUser;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.domain.RequestHolder;
import com.user.springboot.domain.UserInfo;
import com.user.springboot.service.RedisService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AutowireUserInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private RedisService redisService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			AutowireUser autowireUser = method.getAnnotation(AutowireUser.class);
			if (autowireUser == null) {
				// 直接放行,不做任何处理
				return true;
			}
			String token = request.getParameter(autowireUser.value());// 获取令牌信息
			if (token == null) {
				log.info("用户令牌为空.......");
				return returnPage(response);
				
			}
			// 不为空的情况下，判定redis中是否存在该用户,没有则返回
			UserInfo userInfo = redisService.getUserInfoByToken(token);
			if (userInfo == null) {
				log.info("用户令牌redis中不存在.......");
				return returnPage(response);
			}
			log.info("{}方法需要提取用户数据.....线程id...", method.getName(), Thread.currentThread().getId());
			RequestHolder.USER_INFO.set(userInfo);
		}
		return true;
	}

	private boolean returnPage(HttpServletResponse response) {
		PrintWriter out = null;
		try {
			ResponseResult<Object> resultUtil = new ResponseResult<Object>(ResultStatus.USER_IS_NULL, "请登录后再试");
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

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (RequestHolder.USER_INFO.get() != null) {
			log.info("清理本地线程中的用户信息.....");
			RequestHolder.USER_INFO.remove();;// 清除本地线程中的数据,防止内存泄漏
		}
		return;
	}

}
