package com.user.springboot.config;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.chat.springboot.common.TraceThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置线程池
 *
 * @author yangyiwei
 * @date 2018年12月4日
 * @time 下午4:13:19
 */
@Configuration
//@ConfigurationProperties(prefix = "thread.pool")
@PropertySource("classpath:threadPool.property")
@Slf4j
public class ThreadPoolConfig {

    /**
     * 给自己的线程池设置名字
     *
     * @param coreSize
     * @param MaxSize
     * @param waitSize
     * @param keepAliveTime
     * @return
     */
    @Bean("threadPoolExecutor")
    public ThreadPoolExecutor getThreadPool(@Value("${thread.pool.core.size}") int coreSize,
                                            @Value("${thread.pool.max.size}") int MaxSize, @Value("${thread.pool.queen.size}") int waitSize,
                                            @Value("${thread.pool.keep.time}") int keepAliveTime) {
        final AtomicInteger atomicInteger = new AtomicInteger();
        return new ThreadPoolExecutor(coreSize, MaxSize, keepAliveTime, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(waitSize), (Runnable r) -> {
            Thread thread = new Thread(r, "controller-pool-" + atomicInteger.incrementAndGet());
            log.info("mongodb controller create thread....." + thread.getName());
            return thread;
        }, (Runnable r, ThreadPoolExecutor executor) -> {
            if (!executor.isShutdown()) {
                System.out.println("xx任务被丢弃......放弃执行....");
                if (r != null && r instanceof FutureTask) { //如果是submit提交方式，则取消掉任务 防止future.get方法无限阻塞
                    ((FutureTask) r).cancel(true);
                }
            }
        }) {
            protected void beforeExecute(Thread t, Runnable r) {
                log.info("准备执行线程:{}" + Thread.currentThread().getName());
            }

            public void afterExecute(Runnable r, Throwable t) {
                log.info("线程执行完毕:{}" + Thread.currentThread().getName());
            }

            public void terminated() {
                log.info("线程池退出.....");
            }
        };


    }

    /**
     * 配置自定义线程池(定制了异常捕捉功能)
     *
     * @param coreSize
     * @param MaxSize
     * @param waitSize
     * @param keepAliveTime
     * @return
     */
    @Bean("traceThreadPoolExecutor")
    public ThreadPoolExecutor getTraceThreadPool(@Value("${thread.pool.core.size}") int coreSize,
                                                 @Value("${thread.pool.max.size}") int MaxSize, @Value("${thread.pool.queen.size}") int waitSize,
                                                 @Value("${thread.pool.keep.time}") int keepAliveTime) {
        return new TraceThreadPoolExecutor(coreSize, MaxSize, keepAliveTime, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(waitSize));
    }

}
