package com.user.springboot.aop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.annotation.ValidateServiceData;
import com.chat.springboot.common.response.ProjectException;
import com.user.springboot.controller.CheckParam;
import com.user.springboot.controller.PerItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * 校验业务逻辑层基本数据
 */
@Aspect
@Component
@Slf4j
public class ValidateServiceDataAspect {

    /**
     * 定义切入点 拦截所有带@ValidateServiceData注解的方法
     * 1.private final方法无法被代理到 这是因为cglib代理实现方式为继承 无法重写final private
     * 2.方法内部调用该注解不会生效 因为返回的不是代理类对象
     */
    @Pointcut("@annotation(com.chat.springboot.common.annotation.ValidateServiceData)")
    public void validateDataPoint() {

    }

    @Before("validateDataPoint()")
    public void before(JoinPoint joinPoint) {

        Method method = getMethod(joinPoint);
        if (method.getAnnotation(ValidateServiceData.class) == null) { //如果cglib代理被关闭，此处无法获取到注解 放弃代理
            return;
        }

        log.info("方法上存在ValidateServiceData注解.....执行方法参数校验....");
        if (method.getAnnotation(ValidateServiceData.class).openCheckParam()) { //开启参数校验
            log.info("开启checkParam注解...执行参数校验中....");
            validateCheckParam(method, getArgsName(method), joinPoint.getArgs());
            return;
        }

        String[] attributes = method.getAnnotation(ValidateServiceData.class).attributes(); //获取校验项
        String[] argsName = getArgsName(method); //获取参数名称
        Object[] params = joinPoint.getArgs(); //获取参数对象
        if (attributes.length != 0) { //参数校验项 不为空 则校验基本数据
            validateAttributes(attributes, argsName, params);
        }
        //校验集合类数据
        validatePerItem(method, params, argsName);

    }

    /**
     * 获取method对象
     *
     * @param joinPoint
     * @return
     */
    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 如果是接口 则获取实现类
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getMethod(signature.getName(), method.getParameterTypes());
            } catch (Exception e) {
                log.error("lockPoint getMethod", e);
            }
        }
        return method;
    }

    /**
     * 获取方法参数名
     *
     * @param method
     * @return
     */
    private String[] getArgsName(Method method) {
        LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] argsName = localVariableTableParameterNameDiscoverer.getParameterNames(method);
        return argsName;
    }


    /**
     * 校验包装数据类 以及基本对象对象
     *
     * @param attributes 校验的参数项
     * @param argsName   参数属性名
     * @param params     参数对象
     */
    private void validateAttributes(String[] attributes, String[] argsName, Object[] params) {
        JSONObject paramJSON = new JSONObject(); //存储所有参数的key-value
        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {

                if (params[i] instanceof Collection) { //此处为集合类 直接跳过 后续的校验 再跟进
                    continue;
                }

                if (params[i] instanceof String) { //字符串实例

                    paramJSON.put(argsName[i], params[i]);

                } else if (params[i] instanceof Number) { //包装类 或者基本类

                    paramJSON.put(argsName[i], params[i]);

                } else if (params[i] instanceof Map) { //Map对象

                    paramJSON.putAll((Map) params[i]);

                } else if (params[i] instanceof Object) { //普通对象实例

                    if (!CommonValidate.checkObject2JSON(params[i])) {
                        continue;
                    }

                    try {
                        JSONObject json = (JSONObject) JSONObject.toJSON(params[i]);
                        paramJSON.putAll(json);
                    } catch (Exception e) {
                        log.warn("序列化参数出错...跳过序列化");
                    }

                }
            }
        }

        CommonValidate.validateStringParam(paramJSON, attributes);

    }


    /**
     * 校验集合中的对象属性
     *
     * @param method 方法体 为了获取参数注解
     * @param params 具体参数对象
     */
    private void validatePerItem(Method method, Object[] params, String[] argsName) {
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (int j = 0; j < annotations[i].length; j++) {

                if (annotations[i][j] instanceof PerItem) { //存在这个注解 校验每一项集合元素

                    if (params[i] == null) { //对象属性为空

                        throw new ProjectException(10086, "请不要传递空集合");

                    } else if (params[i] instanceof Collection) { //集合类实例

                        PerItem perItem = (PerItem) annotations[i][j];
                        checkCollection((Collection) params[i], argsName[i], perItem.attributes());

                    } else {
                        log.warn("参数上存在@PerItem注解,但不是集合类型,跳过校验");
                    }

                }

            }
        }
    }

    /**
     * 校验存在CheckParam注解的参数
     *
     * @param method
     */
    private void validateCheckParam(Method method, String[] argsName, Object[] params) {
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (int j = 0; j < annotations[i].length; j++) {
                if (annotations[i][j] instanceof CheckParam) { //如果参数上存在 RequestBody的注解。则校验这个参数
                    Object object = params[i];

                    if (object == null) {

                        throw new ProjectException(10086, "参数:" + argsName[i] + "不能为空对象!!!");

                    }

                    if (object instanceof String) { //字符串

                        checkString(object.toString(), argsName[i]);

                    } else if (object instanceof Number) { //基本数据类型以及包装数据类

                        checkNumber((Number) object, argsName[i]);

                    } else if (object instanceof Collection) { //集合

                        CheckParam checkParam = (CheckParam) annotations[i][j];
                        checkCollection((Collection) object, argsName[i], checkParam.attributes());

                    } else if (object instanceof Map) { //Map

                        CheckParam checkParam = (CheckParam) annotations[i][j];
                        checkMap((Map) object, argsName[i], checkParam.attributes());

                    } else if (object instanceof Object) { //普通对象

                        CheckParam checkParam = (CheckParam) annotations[i][j];
                        checkObject(object, argsName[i], checkParam.attributes());
                    }
                }
            }
        }
    }


    /**
     * 校验String
     *
     * @param string  字符串
     * @param argName 参数名
     */
    private void checkString(String string, String argName) {
        if (StringUtils.isBlank(string)) {
            throw new ProjectException(10086, "String参数:" + argName + "不能为空字符串!!!");
        }
    }

    /**
     * 校验基本数据类型
     *
     * @param number  基本数据
     * @param argName 参数名
     */
    private void checkNumber(Number number, String argName) {
        ; //啥也不干 也可以校验是否为0
    }

    /**
     * 校验集合类中对象属性
     *
     * @param collection 集合
     * @param argName    参数名
     * @param attributes 校验的属性
     */
    private void checkCollection(Collection collection, String argName, String[] attributes) {
        JSONArray jsonArray = (JSONArray) JSONArray.toJSON(collection);
        if (jsonArray.size() == 0) {
            throw new ProjectException(10086, "Collection参数:" + argName + "不能为空集合!!!");
        }

        if (attributes.length == 0) { //不存在属性判断 只需要判定空不空 如List<String>
            return;
        }
        for (int x = 0; x < jsonArray.size(); x++) {

            if (!CommonValidate.checkObject2JSON(jsonArray.get(x))) {
                continue;
            }

            JSONObject item;
            try {
                item = jsonArray.getJSONObject(x);
            } catch (Exception e) {
                log.warn("存在某些无法序列化的参数....跳过校验");
                continue;
            }

            for (String attribute : attributes) { //校验参数项
                if (item.get(attribute) == null) {
                    throw new ProjectException(10086, "Collection参数:" + argName + "的对象属性" + attribute + "不能为空");
                } else if (item.get(attribute) instanceof String) {
                    if (StringUtils.isBlank(item.getString(attribute))) { //字符串
                        throw new ProjectException(10086, "Collection参数:" + argName + "的对象属性" + attribute + "不能为空字符串");
                    }
                }
            }
        }
    }

    /**
     * 校验Map对象
     *
     * @param map        Map对象
     * @param argName    参数名
     * @param attributes 校验的参数名
     */
    private void checkMap(Map map, String argName, String[] attributes) {

        if (attributes.length == 0) {
            return;
        }
        // 校验map的key是不是这几个属性
        for (String attribute : attributes) {
            if (map.get(attribute) == null) {
                throw new ProjectException(10086, "Map参数" + argName + "的key:" + attribute + "不能为空");
            }
        }

    }

    /**
     * 校验Object对象
     *
     * @param object
     * @param argName
     * @param attributes
     */
    private void checkObject(Object object, String argName, String[] attributes) {

        if (attributes.length == 0) {
            return;
        }
        if (!CommonValidate.checkObject2JSON(object)) {
            return;
        }

        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) JSONObject.toJSON(object);
        } catch (Exception e) {
            log.warn("存在某些无法序列化的参数....跳过校验");
            return;
        }

        for (String attribute : attributes) {
            if (jsonObject.get(attribute) == null) { //对象中不存在该属性
                throw new ProjectException(10086, "Object参数:" + argName + "的属性" + attribute + "不能为空");
            } else if (jsonObject.get(attribute) instanceof String) {
                if (StringUtils.isBlank(jsonObject.getString(attribute))) {
                    throw new ProjectException(10086, "Object参数:" + argName + "的属性" + attribute + "不能为空字符串");
                }
            }
        }
    }


}
