
package com.user.springboot.controller;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.tuya.crm.core.entity.BaseDO;

/**
 * Created by smartbruce on 2017/4/17.
 */
//@Aspect Mybatis层 我已经做了拦截器 实现该功能 所以注释掉这四个切面
//@Component
public class RepositoryAspect {

	@Around("execution(* com.tuya.crm.core.repository.BaseRepository.insert(..))")
	public Object beforeInsert(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		Object entity = args[0];
		if (entity instanceof BaseDO) {
			BaseDO baseDO = (BaseDO) entity;
			long current = Calendar.getInstance().getTimeInMillis();
			baseDO.setGmtCreated(current);
			baseDO.setGmtModified(current);
		}

		Object retVal = pjp.proceed();
		return retVal;
	}

	@Around("execution(* com.tuya.crm.core.repository.BaseRepository.update(..))")
	public Object beforeUpdate(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		Object entity = args[0];
		if (entity instanceof BaseDO) {
			BaseDO baseDO = (BaseDO) entity;
			baseDO.setGmtModified(Calendar.getInstance().getTimeInMillis());
		}

		Object retVal = pjp.proceed();
		return retVal;
	}

	@Around("execution(* com.tuya.crm.core.repository.BaseRepository.batchInsert(..))")
	public Object beforeBatchInsert(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		Object firstArg = args[0];
		if (firstArg instanceof List) {
			List entityList = (List) firstArg;
			if (CollectionUtils.isNotEmpty(entityList)) {
				long current = Calendar.getInstance().getTimeInMillis();
				for (Object entity : entityList) {
					BaseDO baseDO = (BaseDO) entity;
					baseDO.setGmtCreated(current);
					baseDO.setGmtModified(current);
				}
			}
		}

		Object retVal = pjp.proceed();
		return retVal;
	}

	@Around("execution(* com.tuya.crm.core.repository.BaseRepository.batchUpdate(..))")
	public Object beforeBatchUpdate(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		Object firstArg = args[0];
		if (firstArg instanceof List) {
			List entityList = (List) firstArg;
			if (CollectionUtils.isNotEmpty(entityList)) {
				long current = Calendar.getInstance().getTimeInMillis();
				for (Object entity : entityList) {
					BaseDO baseDO = (BaseDO) entity;
					baseDO.setGmtModified(current);
				}
			}
		}

		Object retVal = pjp.proceed();
		return retVal;
	}
}
