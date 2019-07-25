package com.user.springboot.aop;

import com.chat.springboot.common.response.ProjectException;
import com.chat.springboot.common.response.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;

/**
 * 防止重复提交
 * 处理逻辑 ： 前端先获取一次请求ID 再访问该接口
 */
@Aspect
@Component
@Slf4j
public class RepeatSubmitLimiterAop {


    @Autowired
    private Jedis jedis;

    private final String expression = "@annotation(com.user.springboot.annotation.RepeatSubmitLimiter)";

    // 拦截所有Controller
    @Pointcut(expression)
    public void rlAop() {
    }

    @Before("rlAop()")
    public void doBefore(JoinPoint proceedingJoinPoint) {
        HttpServletRequest request = getRequest();
        String reqId = request.getHeader("reqId");
        if (StringUtils.isEmpty(reqId)) {
            log.warn("请不要重复提交请求....!!!");
            throw new ProjectException(ResultStatus.DEFINE_ERROR);
        }
        // 获取对应的reqId,如果能够获取该reqId，就直接执行具体的业务逻辑
        boolean isFind = delReqId(reqId);
        // 获取对应的reqId,如果获取不到该reqId 直接返回请勿重复提交
        if (!isFind) {
            log.warn("请不要重复提交请求....!!!");
            throw new ProjectException(ResultStatus.DEFINE_ERROR);
        }
    }

    public HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request;
    }


    public boolean delReqId(String reqIdKey) {
        long count = jedis.del(reqIdKey);
        if (count < 1) { //第二次请求 不做处理
            return false;
        }
        return true;
    }

}
