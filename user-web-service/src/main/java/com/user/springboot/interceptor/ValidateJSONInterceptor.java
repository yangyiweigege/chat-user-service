package com.user.springboot.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.chat.springboot.common.annotation.ValidateJSON;

import lombok.extern.slf4j.Slf4j;

/**
 * 校验 application/json请求的 json字段数据
 * @author yangyiwei
 * @date 2018年8月6日
 * @time 上午10:32:10
 */
@Slf4j
public class ValidateJSONInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			ValidateJSON validateJSON = method.getAnnotation(ValidateJSON.class);
			if (validateJSON != null) { // 执行参数拦截
				log.info("此方法上存在 validateJSON注解...开始进行JSON属性校验.....");
				//判定报头信息 是否是application/json
				log.info("请求携带的报头信息:{}", request.getContentType());
				if (!request.getContentType().equals("application/json")) {
					return true; //如果不是application/json请求 直接放行 不做处理
				}
				return false;
				/*String json = getJsonDataForRequest(request);
				log.info("用户上传的JSON数据.....:{}", json);
				String[] attributes = validateJSON.attributes();
				if (attributes.length == 0) {
					return true;// 放行
				} else {  //此处进行jons数据校验
					return true;
				}*/
			}
			return true;
		} // 方法结束
		return true;
	}

	/**
	 * <pre>
	 * 说       明: 从REQUEST中获取JSON数据
	 * 涉及版本: V3.0.0  
	 * 创  建  者: 杨乙伟
	 * 日       期: 2017年12月1日上午9:31:13
	 * </pre>
	 */
	public String getJsonDataForRequest(HttpServletRequest request) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					throw ex;
				}
			}
		}
		return stringBuilder.toString();
	}

}
