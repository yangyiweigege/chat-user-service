package com.user.springboot.aop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.annotation.ValidateJSON;
import com.chat.springboot.common.response.ProjectException;
import com.chat.springboot.common.response.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 校验requestBody注解的参数 是否合理
 */
@Aspect
@Component
@Slf4j
public class ValidateJSONAop {

    /**
     * <pre>
     * 功       能: 统一定义一个切面，复用
     * 涉及版本: V3.0.0
     * 创  建  者: yangyiwei
     * 日       期: 2018年3月9日 上午9:42:27
     * Q    Q: 2873824885
     * </pre>
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void validateJSONPoint() {

    }

    @Before("validateJSONPoint()")
    public void before(JoinPoint joinPoint) {
        Object[] params = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method.getAnnotation(ValidateJSON.class) == null) {
            return;
        }
        String[] attributes = method.getAnnotation(ValidateJSON.class).attributes();
        if (attributes.length == 0) {
            return;
        }
        log.info("本次请求.....执行json参数校验.....");
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (int j = 0; j < annotations[i].length; j++) {
                if (annotations[i][j] instanceof RequestBody) { //如果参数上存在 RequestBody的注解。则校验这个参数
                    Object object = params[i];
                    if (object instanceof String) { //如果是字符串,则转json
                        JSONObject jsonObject = JSONObject.parseObject(object.toString());
                        for (String attribute : attributes) {
                            if (jsonObject.get(attribute) == null) {
                                throw new ProjectException(attribute + "属性不能为空!", 10086);
                            }
                        }
                    } else if (object instanceof Number) { //如果是包装类

                        continue;

                    } else if (object instanceof Collection) { //如果是数组

                        JSONArray jsonArray = (JSONArray) JSONArray.toJSON(object);
                        for (int x = 0; x < jsonArray.size(); x++) { //对每一项属性进行校验

                        }

                    } else { //普通对象 或者 基本数据类型

                        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
                        for (String attribute : attributes) {
                            Object param = jsonObject.get(attribute);
                            if (param == null) {
                                throw new ProjectException(ResultStatus.LACK_PARAM, attribute + "属性不能为空!");
                            } else if (param instanceof String) {

                                if (StringUtils.isBlank(param.toString())) {
                                    throw new ProjectException(ResultStatus.LACK_PARAM, attribute + "属性不能为空字符串!");
                                }

                            }
                        }

                    }
                }
            }
        }
        //joinPoint.get


    }


}
