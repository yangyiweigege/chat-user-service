package com.user.springboot.controller;//package com.tuya.crm.core.advice;
//
//import com.alibaba.fastjson.JSON;
//import com.tuya.crm.client.domain.vo.stock.StockRecordVO;
//import com.tuya.crm.common.common.BusinessConstants;
//import com.tuya.crm.common.common.CrmConstants;
//import com.tuya.crm.common.exception.BizException;
//import com.tuya.crm.common.exception.BizExceptionCode;
//import com.tuya.crm.common.permission.DataPermissionTypeEnum;
//import com.tuya.crm.common.utils.EmptyUtils;
//import com.tuya.crm.core.common.RedisManager;
//import org.apache.commons.lang3.StringUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.Signature;
//import org.aspectj.lang.annotation.*;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
//import org.springframework.expression.EvaluationContext;
//import org.springframework.expression.Expression;
//import org.springframework.expression.ExpressionParser;
//import org.springframework.expression.spel.standard.SpelExpressionParser;
//import org.springframework.expression.spel.support.StandardEvaluationContext;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.lang.reflect.Method;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * Created by hulda on 2019-03-05
// */
//@Deprecated
//@Aspect
//@Component
//public class ConcurrencyLockAspect {
//    private static final Logger logger = LoggerFactory.getLogger(ConcurrencyLockAspect.class);
//
//    @Autowired
//    private RedisManager redisManager;
//
//    @Autowiredkj
//    private RedissonClient redissonClient;
//
//    @Pointcut(value = "@annotation(concurrencyLock)", argNames = "concurrencyLock")
//    public void rlAop(ConcurrencyLock concurrencyLock) {
//    }
//
//    @Around("rlAop(concurrencyLock)")
//    public Object addLock(ProceedingJoinPoint proceedingJoinPoint,ConcurrencyLock concurrencyLock) throws Throwable {
//        Object[] args = proceedingJoinPoint.getArgs();
//        String lockKey = concurrencyLock.lockKey();
//        RLock resultLock = null;Object returnValue = null;
//        try {
//            resultLock = redissonClient.getFairLock("1111");
//            resultLock.lock(CrmConstants.TEN, TimeUnit.MINUTES);
//            returnValue = proceedingJoinPoint.proceed();
//            resultLock.unlock();
//        } catch (Exception exp){
//            if (!EmptyUtils.isAnyoneEmpty(resultLock)) {
//                resultLock.unlock();
//            }
//            throw new BizException(BizExceptionCode.LOCK_EXCEPTION);
//        }
//        return returnValue;
//    }
//}
