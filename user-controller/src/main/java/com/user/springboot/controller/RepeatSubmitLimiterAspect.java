package com.user.springboot.controller;

import com.chat.springboot.common.response.ProjectException;
import com.chat.springboot.common.response.ResultStatus;
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
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
@Aspect
@Component
public class RepeatSubmitLimiterAspect {
    private static final Logger logger = LoggerFactory.getLogger(RepeatSubmitLimiterAspect.class);

    private Lock lock = new ReentrantLock();

    @Autowired
    private Jedis jedis;

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
            throw new ProjectException(ResultStatus.DEFINE_ERROR);
        }
        // 获取对应的reqId,如果能够获取该reqId，就直接执行具体的业务逻辑
        boolean isFind = findReqId(reqId);
        // 获取对应的reqId,如果获取不到该reqId 直接返回请勿重复提交
        if (!isFind) {
            throw new ProjectException(ResultStatus.DEFINE_ERROR);
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
        long count = jedis.del(reqIdKey);
        if (count < 1) { //第二次请求 不做处理
            return false;
        }
        return true;
    }

}
