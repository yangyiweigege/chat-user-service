package com.user.springboot.interceptor;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.annotation.ValidateAttribute;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;



import lombok.extern.slf4j.Slf4j;

/**
 * 校验属性不为空字段拦截器
 * 
 * @author yangyiwei
 * @date 2018年8月1日
 * @time 下午4:16:38
 */
@Slf4j
public class ValidateAttributeInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			ValidateAttribute validateAttribute = method.getAnnotation(ValidateAttribute.class);
			if (validateAttribute != null) { // 执行参数拦截
				log.info("此方法上存在 validateAttribute注解...开始进行字段校验.....");
				String[] attributes = validateAttribute.attributes();
				if (attributes.length == 0) {
					return true;// 放行
				} else {
					for (int i = 0; i < attributes.length; i++) {
						String param = request.getParameter(attributes[i]);
						if (param == null || "".equals(param)) { // 参数校验不通过
							response.setContentType("application/json;charset=utf-8");
							PrintWriter out = null;
							try {
								ResponseResult<String> responseResult = new ResponseResult<String>(
										ResultStatus.LACK_PARAM, attributes[i] + "字段不能为空!");
								log.info("用户并未上传 {}字段....校验不通过", attributes[i]);
								out = response.getWriter();
								out.write(JSONObject.toJSONString(responseResult));
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
		return true;
	}// 方法结束

}
