package com.user.springboot.task;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.user.springboot.cache.JVM_CACHE;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

/**
 * 配置定时任务
 * @author yangyiwei
 * @date 2018年12月4日
 * @time 下午5:25:45
 */
@Component
@Slf4j
public class ScheduledTask {

	@Autowired
	@Qualifier("threadPoolExecutor")
	private ThreadPoolExecutor executorService;
	@Autowired
	private JedisPool jedisPool;

	@Scheduled(fixedRate = 1000 * 60 * 30)
	public void showThreadPoolStatus() {
		log.info("=====================监控线程池运行状况======================");
		log.info("线程池任务总数:{},完成的任务数:{},线程池当前线程数:{},当前正在执行任务的线程数:{}" ,executorService.getTaskCount()
				, executorService.getCompletedTaskCount(), executorService.getPoolSize(), executorService.getActiveCount()
				);
	}
	
	/**
	 * 定期展示每个接口访问次数
	 * 1000ms * 60s * 30
	 */
	@Scheduled(fixedRate = 1000 * 60 * 30)
	public void showCountInterface() {
		Map<String, String> execTimeMap = new HashMap<>();
		Map<String, String> execSlowMap = new HashMap<>();
		for (Map.Entry<String, AtomicInteger> item : JVM_CACHE.countInterface.entrySet()) {
			log.info("接口:{}, 累计访问次数:{}", item.getKey(), item.getValue().get());
			execTimeMap.put(item.getKey(), item.getValue().get() + "");
		}
		if (JVM_CACHE.execSlowInterface.size() != 0) {
			log.info("======以下接口执行时间较长,最好能优化一下========");
			for (Map.Entry<String, Integer> item : JVM_CACHE.execSlowInterface.entrySet()) {
				log.info("耗时接口:{}, 最近一次调用耗时:{}ms", item.getKey(), item.getValue());
				execSlowMap.put(item.getKey(), item.getValue() + "");
			}
		}
		
		/**此处可以定期将结果写入redis**/
		Jedis jedis = jedisPool.getResource();
		Pipeline pipeline = jedis.pipelined();
		pipeline.hmset("interface_invoke_count", execTimeMap); //接口调用次数
		pipeline.hmset("slow_exec_interface", execSlowMap);//执行慢的接口
		pipeline.syncAndReturnAll();//所有结果写入redis
		jedis.disconnect();

	}
}
