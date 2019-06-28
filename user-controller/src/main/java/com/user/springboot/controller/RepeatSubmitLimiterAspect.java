package com.user.springboot.controller;

import com.tuya.crm.common.exception.BizException;
import com.tuya.crm.common.exception.BizExceptionCode;
import com.tuya.crm.core.common.RedisManager;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuweiqiang on 2019-02-18.
 */
@Aspect
@Component
public class RepeatSubmitLimiterAspect {
    private static final Logger logger = LoggerFactory.getLogger(RepeatSubmitLimiterAspect.class);

    private Lock lock = new ReentrantLock();

    @Autowired
    private RedisManager redisManager;

    private final String expression = "@annotation(com.tuya.crm.core.advice.RepeatSubmitLimiter)";

    // 拦截所有Controller
    @Pointcut(expression)
    public void rlAop() {
    }

    @Around("rlAop()")
    public Object doBefore(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        HttpServletRequest request = getRequest();
        String reqId = request.getHeader("reqId");
        if (StringUtils.isEmpty(reqId)) {
            throw new BizException(BizExceptionCode.REPEAT_SUBMIT_FORM_REQID_NOT_NULL);
        }
        // 获取对应的reqId,如果能够获取该reqId，就直接执行具体的业务逻辑
        boolean isFind = findReqId(reqId);
        // 获取对应的reqId,如果获取不到该reqId 直接返回请勿重复提交
        if (!isFind) {
            throw new BizException(BizExceptionCode.REPEAT_SUBMIT_FORM_NOT_ALLOW);
        }
        Object proceed = proceedingJoinPoint.proceed();
        return proceed;
    }

    public HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request;
    }


    public boolean findReqId(String reqIdKey) {
        // TODO @强哥 此处代码可能存在线程安全问题 但目前场景 几乎不会 以后优化 ---来自葱鸭的留言
        try {
            lock.lock();
            String reqId = (String) redisManager.get(reqIdKey);
            if (StringUtils.isEmpty(reqId)) {
                return false;
            }
            redisManager.delete(reqId);
        } finally {
            lock.unlock();
        }
        return true;
    }

}
