package com.user.springboot.aop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.annotation.ValidateRequestBody;
import com.chat.springboot.common.response.ProjectException;
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
 * 校验RequestBody注解的参数 是否合理
 */
@Aspect
@Component
@Slf4j
public class ValidateRequestBodyAop {

    /**
     * 定义切入点
     */
    @Pointcut("@annotation(com.chat.springboot.common.annotation.ValidateRequestBody)")
    public void validateJSONPoint() {

    }

    @Before("validateJSONPoint()")
    public void before(JoinPoint joinPoint) {
        log.info("方法上存在ValidateJSON注解.....执行RequestBody参数校验....");
        Object[] params = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        String[] attributes = method.getAnnotation(ValidateRequestBody.class).attributes();
        if (attributes.length == 0) { // 未填写校验参数项 TODO 以后会兼容List.size > 0  对象 !=null 的校验
            return;
        }

        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (int j = 0; j < annotations[i].length; j++) {
                if (annotations[i][j] instanceof RequestBody) { //如果参数上存在 RequestBody的注解。则校验这个参数
                    Object object = params[i];

                    if (object == null) {
                        throw new ProjectException(10086, "存在@RequestBody的对象不能为空!");
                    }

                    if (object instanceof String) { //如果是字符串

                        JSONObject jsonObject = JSONObject.parseObject(object.toString());
                        CommonValidate.validateStringParam(jsonObject, attributes);

                    } else if (object instanceof Number) { //如果是包装类

                        validatePackageParam();

                    } else if (object instanceof Collection) { //如果是数组

                        JSONArray jsonArray = (JSONArray) JSONArray.toJSON(object);
                        validateListParam(jsonArray, attributes);

                    } else if (object instanceof Object) { //普通对象

                        if (!CommonValidate.checkObject2JSON(object)) {
                            continue;
                        }

                        JSONObject jsonObject;
                        try {
                            jsonObject = (JSONObject) JSONObject.toJSON(object);
                        } catch (Exception e) {
                            log.warn("在某些无法序列化的参数....跳过校验");
                            continue;
                        }
                        validateObjectParam(jsonObject, attributes);

                    }
                }
            }
        }
    }




    /**
     * 校验参数是list数据类型
     */
    private void validateListParam(JSONArray jsonArray, String[] validateAttribute) {
        if (jsonArray.size() == 0) {
            throw new ProjectException(10086, "批量操作最少需要一条数据");
        }

        for (int x = 0; x < jsonArray.size(); x++) { //对每一项属性进行校验
            for (String attribute : validateAttribute) {


                if (!CommonValidate.checkObject2JSON(jsonArray.get(x))) {
                    continue;
                }

                JSONObject jsonObject;
                try {
                    jsonObject = jsonArray.getJSONObject(x);
                } catch (Exception e) {
                    log.warn("在某些无法序列化的参数....跳过校验");
                    continue;
                }


                if (jsonObject.get(attribute) == null) {
                    throw new ProjectException(10086, attribute + "属性不能为空!");
                } else if (jsonObject.get(attribute) instanceof String) {
                    if (StringUtils.isBlank(jsonObject.getString(attribute))) {
                        throw new ProjectException(10086, attribute + "属性不能为空字符串!");
                    }
                }
            }
        }
    }

    /**
     * 校验普通对象
     * @param jsonObject
     */
    private void validateObjectParam(JSONObject jsonObject, String[] validateAttribute) {
        for (String attribute : validateAttribute) {
            Object param = jsonObject.get(attribute);
            if (param == null) {
                throw new ProjectException(10086, attribute + "属性不能为空!");
            } else if (param instanceof String) {

                if (StringUtils.isBlank(param.toString())) {
                    throw new ProjectException(10086, attribute + "属性不能为空字符串!");
                }

            }
        }
    }

    /**
     * 校验包装类型 直接跳过
     */
    private void validatePackageParam() {
        //throw new ProjectException("10086", "不支持基本数据类型以及包装类");
    }


}
