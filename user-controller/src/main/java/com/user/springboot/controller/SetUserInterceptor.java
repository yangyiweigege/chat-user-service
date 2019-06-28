package com.user.springboot.controller;
import com.tuya.crm.core.common.ContextUtils;
import com.tuya.crm.core.entity.BaseDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * 数据库插入 更新 时 设置用户信息 以及创建时间 修改时间.
 * 备注:如果是dubbo接口 默认设置SYSTEM -1
 * (如有必要 可以在此处对sql进行扩充)
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
//@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class SetUserInterceptor implements Interceptor {

    @SuppressWarnings("unchecked")
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];
        Object parameterObject = args[1];
        String id = statement.getId(); //获取sqlId名称

        //判断sql是否需要拦截.此处只拦截update和insert
        if (!id.toLowerCase().contains("insert") && !id.toLowerCase().contains("update")) {
            return invocation.proceed();
        }

        BoundSql boundSql = statement.getBoundSql(parameterObject);
        // 判断参数获取类型
        Object parameter = boundSql
                .getParameterObject();
        /// log.info("当前参数类型:{}, 当前参数属性:{}", parameter.getClass().getName(), parameter.toString());

        if (parameter instanceof BaseDO) { //单条

            BaseDO baseDO = (BaseDO) parameter;
            if (id.toLowerCase().contains("insert")) { //插入
                setCreateUserInfo(baseDO);
            } else if (id.toLowerCase().contains("update")) { //更新
                setModifyUserInfo(baseDO);
            }

        } else if (parameter instanceof Collection) { //批量List

            Collection collection = (Collection) parameter;
            //遍历集合 设置属性
            if (id.toLowerCase().contains("insert")) { //插入
                for (Object item : collection) {
                    if (item instanceof BaseDO) {
                        BaseDO baseDO = (BaseDO) item;
                        setCreateUserInfo(baseDO);
                    }
                }
            } else if (id.toLowerCase().toLowerCase().contains("update")) { //更新
                for (Object item : collection) {
                    if (item instanceof BaseDO) {
                        BaseDO baseDO = (BaseDO) item;
                        setModifyUserInfo(baseDO);
                    }
                }
            }

        } else if (parameter instanceof Map) { //批量 Mybatis默认机制会将 list 转为map存储

            Map<String, Object> map = (Map<String, Object>) parameter;
            for (Object object : map.values()) {
                if (object instanceof Collection) { //是list
                    Collection collection = (Collection) object; //判断每一项是否继承自DO
                    if (id.toLowerCase().contains("insert")) {
                        for (Object item : collection) {
                            if (item instanceof BaseDO) {
                                BaseDO baseDO = (BaseDO) item;
                                setCreateUserInfo(baseDO);
                            }
                        }

                    } else if (id.toLowerCase().contains("update")) {
                        for (Object item : collection) {
                            if (item instanceof BaseDO) {
                                BaseDO baseDO = (BaseDO) item;
                                setModifyUserInfo(baseDO);
                            }
                        }
                    }
                }
            }

        }
        return invocation.proceed();
    }

    public void setCreateUserInfo(BaseDO baseDO) {
        if (baseDO.getGmtModified() == null) {
            baseDO.setGmtModified(Calendar.getInstance().getTimeInMillis());
        }
        if (baseDO.getGmtCreated() == null) {
            baseDO.setGmtCreated(Calendar.getInstance().getTimeInMillis());
        }

        if (baseDO.getIsDeleted() == null) {
            baseDO.setIsDeleted("N");
        }

        if (ContextUtils.getCurrentUser() == null) { //dubbo调用
            setCreateUser(baseDO, "system", 0L);
        } else {
            setCreateUser(baseDO, ContextUtils.getCurrentUser().getNick(), ContextUtils.getCurrentUser().getId());
        }

    }

    private void setCreateUser(BaseDO baseDO, String userName, Long userId) {
        if (baseDO.getCreator() == null) {
            baseDO.setCreator(userName);
        }
        if (baseDO.getModifier() == null) {
            baseDO.setModifier(userName);
        }
        if (baseDO.getCreatorUid() == null) {
            baseDO.setCreatorUid(userId);
        }
        if (baseDO.getModifierUid() == null) {
            baseDO.setModifierUid(userId);
        }
    }

    public void setModifyUserInfo(BaseDO baseDO) {
        if (baseDO.getGmtModified() == null) {
            baseDO.setGmtModified(Calendar.getInstance().getTimeInMillis());
        }

        if (ContextUtils.getCurrentUser() == null) { //dubbo调用
            setModifyUser(baseDO, "system", 0L);
        } else {
            setModifyUser(baseDO, ContextUtils.getCurrentUser().getNick(), ContextUtils.getCurrentUser().getId());
        }


    }

    private void setModifyUser(BaseDO baseDO, String userName, Long userId) {

        if (baseDO.getModifier() == null) {
            baseDO.setModifier(userName);
        }

        if (baseDO.getModifierUid() == null) {
            baseDO.setModifierUid(userId);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties arg0) {

    }

}
