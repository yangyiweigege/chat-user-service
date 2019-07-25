package com.user.springboot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import java.lang.reflect.Method;

/**
 * JoinPoint公共操作
 */
@Slf4j
public class CommonJoinPointOperation {


    /**
     * 通过joinPoint获取 method
     * 如果是接口 会自动获取实现类的方法实现（解决JDK动态代理 无法获取实现类方法的注解问题）
     * @param joinPoint
     * @return
     */
    public static Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
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
    public static String[] getArgsName(Method method) {
        LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] argsName = localVariableTableParameterNameDiscoverer.getParameterNames(method);
        return argsName;
    }

}
