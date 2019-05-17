package  com.user.springboot.aop;

import com.chat.springboot.common.response.ProjectException;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 捕捉代码异常 但无法捕捉到拦截器异常
 * 
 * @author yangyiwei
 * @date 2018年11月19日
 * @time 下午4:02:44
 */
@Aspect
@Component
@Slf4j
//@Order(0) //确保异常捕捉机制属于最顶层,在事物回滚 或者 提交后 触发这个切面
public class DealExceptionAop implements Ordered {

	/**
	 * 定义切入点 捕捉catchDubboException
	 */
	@Pointcut("@annotation(com.user.springboot.service.impl.CatchDubboException)")
	public void dealExceptionAop () {

	}

	/*@AfterReturning("dealExceptionAop()")
	public void afterThrow(Throwable e) {

	}*/

	@Around("dealExceptionAop()")
	public Object catchExceptionAndDeal(ProceedingJoinPoint joinPoint) {
		// 修改处理后的结果 然后调用 methon.invoke执行
		Object retVal;
		try {
			log.info("执行方法异常捕捉切面...");
			retVal = joinPoint.proceed(joinPoint.getArgs());
			return retVal;
		} catch (Throwable e) {
			if (e instanceof ProjectException) { //此处自定义项目异常
				ProjectException projectException = (ProjectException) e;
				log.info("业务异常....原因是:{}", e);
				return new ResponseResult(projectException.getResultStatus());
			//	return CommonResult.newInstance("10086", e.getMessage());

			} else {
				log.error("服务器发生未知错误======!!", e);
			//	return "发生错误了.laotie ";
				return new ResponseResult<>(ResultStatus.DATA_NOT_FIND);
				//return CommonResult.newInstance("10086", "服务器发生未知异常,请联系开发人员");
			}
		}
		
	}

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
}
