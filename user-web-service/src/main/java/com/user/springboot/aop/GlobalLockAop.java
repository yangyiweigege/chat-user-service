package com.user.springboot.aop;

import com.chat.springboot.common.response.ProjectException;
import com.user.springboot.annotation.GlobalLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁 切面
 */
@Aspect
@Slf4j
public class GlobalLockAop {


    @Autowired
    @Qualifier("redissonClient")
    private RedissonClient redisson;

    /**
     * 定义切入点
     */
    @Pointcut("@annotation(com.user.springboot.annotation.GlobalLock)")
    public void globalLockPoint() {

    }

    @Around("globalLockPoint()")
    public Object invokeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = CommonJoinPointOperation.getMethod(joinPoint);
        GlobalLock distributedLock = method.getAnnotation(GlobalLock.class);

        String lockPath = parseLockPath(joinPoint, method, distributedLock); //解析 锁定的key

        RLock lock = redisson.getLock(lockPath);
        if (lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime() * 1000, TimeUnit.MILLISECONDS)) {
            log.info("成功获取分布式锁...锁定的key:{}", lockPath);
            try {
                return joinPoint.proceed();
            } finally { //方法执行完后 释放锁
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.info("释放分布式锁...释放的key:{}", lockPath);
                }
            }
        } else {
            throw new ProjectException(1111, "有用户正在操作当前资源 请稍后再试");
        }

    }

    /**
     * 返回需要锁定的 key
     * @param joinPoint
     * @param method
     * @param distributedLock
     * @return
     */
    private String parseLockPath(ProceedingJoinPoint joinPoint, Method method, GlobalLock distributedLock) {
        String lockPath = distributedLock.path();

        if (StringUtils.isBlank(lockPath)) {
            lockPath = joinPoint.getTarget().getClass().getSimpleName() + method.getName();
        }

        if (StringUtils.isNotBlank(distributedLock.key())) {
            String key = parseKey(distributedLock.key(), method, joinPoint.getArgs());
            lockPath = lockPath + "_" +key;
        }

        return lockPath;
    }

    /**
     * 通过spring 解析Eval 表达式
     * @param key
     * @param method
     * @param args
     * @return
     */
    private String parseKey(String key, Method method, Object[] args) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("锁路径(lockPath)不能为空");
        }

        String[] paraNameArr = CommonJoinPointOperation.getArgsName(method);

        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }


}
