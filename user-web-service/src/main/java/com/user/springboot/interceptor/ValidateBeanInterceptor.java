package com.user.springboot.interceptor;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.annotation.CheckValue;
import com.chat.springboot.common.annotation.ValidateBean;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;


/**
 * <pre>
 * 功       能: 校验实体的参数拦截器(校验实体类上存在checkValue注解的属性)
 * 涉及版本: V2.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2017年10月19日 上午10:11:19
 * Q     Q: 2873824885
 * </pre>
 */
public class ValidateBeanInterceptor extends HandlerInterceptorAdapter {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("执行validateBean拦截器!" + request.getClass().getName());
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			Annotation[][] annotations = method.getParameterAnnotations(); // 获取所有参数注解
			Class<?>[] params = method.getParameterTypes();// 获取所有参数类型
			for (int i = 0; i < annotations.length; i++) {
				for (int j = 0; j < annotations[i].length; j++) {
					// 判断是否是ValidateBean注解，存在则执行校验
					if (annotations[i][j] instanceof ValidateBean) {
						for (Field field : params[i].getDeclaredFields()) {
							CheckValue checkValue = field.getAnnotation(CheckValue.class);
							if (checkValue != null) {
								String fieldName = request.getParameter(field.getName());
								if (fieldName == null) {
									System.out.println(field.getName() + "字段未通过校验");
									response.setContentType("text/html;charset=utf-8");
									PrintWriter out = null;
									try {
										ResponseResult<Object> resultUtil = new ResponseResult<>();
										resultUtil.setCode(ResultStatus.LACK_PARAM)
												.setData(field.getName() + "字段未通过校验");
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
							}
						}
					}
				}
			}
		} // if 结束
		return super.preHandle(request, response, handler);
	}// 方法结束

	/**
	 * 日后扩展备用
	 * 
	 * <pre>
	 * 功       能: 
	 * 涉及版本: V2.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2017年10月19日 下午1:19:22
	 * Q     Q: 2873824885
	 * </pre>
	 */
	private void validate() {

	}
}
