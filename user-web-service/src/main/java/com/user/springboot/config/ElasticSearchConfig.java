package com.user.springboot.config;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * ElasticSearch的配置
 * 
 * @author yangyiwei
 * @date 2018年9月3日
 * @time 上午10:54:57
 */
@Configuration
@Slf4j
public class ElasticSearchConfig {

	@Bean
	public TransportClient client() throws UnknownHostException {
		log.info("开始初始化elasticsearch。。。。");
		TransportClient transportClient = null;
		try {
			// 配置信息
			Settings esSetting = Settings.builder().put("cluster.name", "elasticsearch") // 集群名字
					.put("client.transport.sniff", true)// 增加嗅探机制，找到ES集群
					// .put("thread_pool.search.size",
					// Integer.parseInt(poolSize))//增加线程池个数，暂时设为5
					.build();
			// 配置信息Settings自定义
			transportClient = new PreBuiltTransportClient(esSetting);
			TransportAddress transportAddress = new TransportAddress(InetAddress.getByName("172.172.1.143"), 9300);
			transportClient.addTransportAddresses(transportAddress);
		} catch (Exception e) {
			log.info("初始化elastic。。失败");
		}
		return transportClient;
	}
}
