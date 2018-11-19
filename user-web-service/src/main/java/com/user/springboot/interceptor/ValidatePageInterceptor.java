package com.user.springboot.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.chat.springboot.common.annotation.ValidatePage;

/**
 * <pre>
 * 功       能:校验参数拦截器 
 * 涉及版本: V2.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2017年10月18日 下午6:38:34
 * Q     Q: 2873824885
 * </pre>
 */
public class ValidatePageInterceptor extends HandlerInterceptorAdapter {

	private final static Logger logger = Logger.getLogger(HandlerInterceptorAdapter.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("执行分页参数校验.....");
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			ValidatePage validate = method.getAnnotation(ValidatePage.class);
			if (validate != null) {
				logger.info("此方法存在 ValidatePage注解...进行分页参数校验中......");
				String pageNo = request.getParameter("pageNo");
				String pageSize = request.getParameter("pageSize");
				// 校验拦截到的参数
				/*
				 * if (pageNo == null || pageSize == null) {
				 * response.setContentType("text/html;charset=utf-8");
				 * PrintWriter out = null; try { Result resultUtil = new
				 * Result(); resultUtil.setCode(ResultStatus.LACK_PARAM).
				 * setData("pageSize 或者 pageNo 不能为空"); out =
				 * response.getWriter();
				 * out.write(JSONObject.toJSONString(resultUtil)); out.flush();
				 * out.close(); } catch (Exception e) { e.printStackTrace();
				 * out.write("服务器异常!"); out.flush(); out.close(); } return
				 * false; }
				 */
				/*
				 * Map<String, String[]> dataMap = request.getParameterMap();
				 * Class<?> dataClass = dataMap.getClass(); Field field =
				 * dataClass.getDeclaredField("locked");
				 * field.setAccessible(true); field.set(dataMap, false);//
				 * 设置locked状态为false解除锁定
				 */ if (pageNo == null) {
					ArrayList<String> list = new ArrayList<String>();
					list.add("1");
					getParamters(request).put("pageNo", list);
					/*
					 * Method putMethod = dataClass.getMethod("put",
					 * Object.class, Object.class); putMethod.invoke(dataMap,
					 * "pageNo", "1");
					 */
				}
				if (pageSize == null) {
					ArrayList<String> list = new ArrayList<String>();
					list.add("10");
					getParamters(request).put("pageSize", list);
					/*
					 * Method putMethod = dataClass.getMethod("put",
					 * Object.class, Object.class); putMethod.invoke(dataMap,
					 * "pageSize", "10");
					 */
				}
				// field.set(dataMap, true);// 修改数据后 切换回原状态 否则所有请求参数丢失
				// 看看request请求
			}
		}
		return true;
	}

	/**
	 * 通过反射获取 request类中的 map
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, ArrayList<String>> getParamters(HttpServletRequest request) {
		Field requestField;
		Field parametersParsedField;
		Field coyoteRequestField;
		Field parametersField;
		Field hashTabArrField;
		try {
			Class<?> clazz = Class.forName("org.apache.catalina.connector.RequestFacade");
			requestField = clazz.getDeclaredField("request");
			requestField.setAccessible(true);

			parametersParsedField = requestField.getType().getDeclaredField("parametersParsed");
			parametersParsedField.setAccessible(true);

			coyoteRequestField = requestField.getType().getDeclaredField("coyoteRequest");
			coyoteRequestField.setAccessible(true);

			parametersField = coyoteRequestField.getType().getDeclaredField("parameters");
			parametersField.setAccessible(true);

			hashTabArrField = parametersField.getType().getDeclaredField("paramHashValues");
			// hashTabArrField =
			// parametersField.getType().getDeclaredField("paramHashStringArray");
			hashTabArrField.setAccessible(true);
			// 修改parma操作
			Object innerRequest = requestField.get(request);
			parametersParsedField.setBoolean(innerRequest, true);
			Object coyoteRequestObject = coyoteRequestField.get(innerRequest);
			Object parameterObject = parametersField.get(coyoteRequestObject);
			return (Map<String, ArrayList<String>>) hashTabArrField.get(parameterObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}