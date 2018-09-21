package com.user.springboot.config;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import com.chat.springboot.common.jms.QueueName;


/**
 * 配置消息中间件activemq
 * @author yangyiwei
 * @date 2018年6月4日
 * @time 下午1:46:00
 */
//@Configuration
public class ActiveMQConfig {

	@Bean("logQueue")
	public Queue logQueue() {
		return new ActiveMQQueue(QueueName.LOG_QUEUE);
	}

	@Bean("logTopic")
	public ActiveMQTopic logTopic() {
		return new ActiveMQTopic(QueueName.LOG_TOPIC);
	}

	/**
	 * <pre>
	 * 功       能: 主题模式的监听
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年5月3日 下午1:31:43
	 * Q    Q: 2873824885
	 * </pre>
	 */
	@Bean("jmsListenerContainerTopic")
	public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory) {
		DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
		bean.setPubSubDomain(true);
		bean.setConnectionFactory(activeMQConnectionFactory);
		return bean;
	}
	
	/**
	 * <pre>
	 * 功       能: 队列模式的监听
	 * 涉及版本: V3.0.0 
	 * 创  建  者: yangyiwei
	 * 日       期: 2018年5月3日 下午1:31:43
	 * Q    Q: 2873824885
	 * </pre>
	 */
	@Bean("jmsListenerContainerQueue")
	public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ConnectionFactory activeMQConnectionFactory) {
		DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
		bean.setConnectionFactory(activeMQConnectionFactory);
		return bean;
	}
}
