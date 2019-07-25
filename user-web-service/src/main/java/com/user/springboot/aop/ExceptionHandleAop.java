package com.user.springboot.aop;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;
import com.chat.springboot.common.response.ProjectException;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 功       能: 统一异常处理 处于所有组件最高层 可以捕获拦截器异常
 * 涉及版本: V3.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2018年3月9日 下午1:45:31
 * Q    Q: 2873824885
 * </pre>
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandleAop {

	@ResponseBody
	@ExceptionHandler(value = Exception.class)
	public ResponseResult<?> handle(Exception e) {
		if (e instanceof ProjectException) {
			log.warn("自定义业务异常:{}", e.getMessage());
			ProjectException projectException = (ProjectException) e;
			ResultStatus resultStatus = projectException.getResultStatus();
			if (projectException.getDetailMsg() != null) {
				return new ResponseResult<String>(resultStatus, projectException.getDetailMsg());
			}
			return new ResponseResult<>(resultStatus);
		}
		log.error("出现了系统未知的错误-----！！！！", e);
		e.printStackTrace();// 未知错误，打印出来
		return new ResponseResult<>(ResultStatus.UNKNOW_ERROR);
	}
}
