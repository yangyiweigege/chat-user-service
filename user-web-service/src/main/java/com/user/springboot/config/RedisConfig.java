package com.user.springboot.config;

import java.lang.reflect.Method;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * <pre>
 * 功       能: 通过configuration value bean注解 将自定义bean托管给spring
 * 涉及版本: V3.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2018年3月29日 上午11:14:02
 * Q    Q: 2873824885
 * </pre>
 */
@Configuration
@PropertySource("classpath:redis.property")
@EnableCaching
public class  RedisConfig extends CachingConfigurerSupport {

	@Bean(name = "jedisPool")
	public JedisPool jedisPool(@Qualifier("jedisPoolConfig") JedisPoolConfig config,
			@Value("${jedis.pool.host}") String host, @Value("${jedis.pool.port}") int port,
			@Value("${jedis.pool.timeout}") int timeout, @Value("${jedis.pool.password}") String password) {
		return new JedisPool(config, host, port, timeout, password);
	}

	@Bean(name = "jedis")
	public Jedis jedis(@Qualifier("jedisPool") JedisPool jedisPool) {
		return jedisPool.getResource();// 返回一条长用连接
	}

	@Bean(name = "jedisPoolConfig")
	public JedisPoolConfig jedisPoolConfig(@Value("${jedis.pool.config.maxTotal}") int maxTotal,
			@Value("${jedis.pool.config.maxIdle}") int maxIdle,
			@Value("${jedis.pool.config.maxWaitMillis}") int maxWaitMillis) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		return config;
	}

	@Bean(name = "redissonClient")
	public RedissonClient getRedisson(@Value("${jedis.pool.host}")String host,
									  @Value("${jedis.pool.port}") String port,
									  @Value("${jedis.pool.password}") String password) {

		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);
		//        config.useMasterSlaveServers().setMasterAddress("").setPassword("").addSlaveAddress(new String[]{"",""});

		return Redisson.create(config);
	}


	
	/* 定义缓存数据 key 生成策略的bean 包名+类名+方法名+所有参数 此方法不建议使用 因为返回的类是动态代理类*/
	@Bean
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(target.getClass().getName());
				for (Object obj : params) {
					sb.append(obj);
				}
				System.out.println("缓存的key是：" + sb.toString());
				return sb.toString();
			}
		};
	}

	/*
	 * 要启用spring缓存支持,需创建一个 CacheManager的 bean，CacheManager 接口有很多实现，这里Redis 的集成，用
	 * RedisCacheManager这个实现类 Redis 不是应用的共享内存，它只是一个内存服务器，就像 MySql 似的，
	 * 我们需要将应用连接到它并使用某种“语言”进行交互，因此我们还需要一个连接工厂以及一个 Spring 和 Redis 对话要用的
	 * RedisTemplate， 这些都是 Redis 缓存所必需的配置，把它们都放在自定义的 CachingConfigurerSupport 中
	 */
	@Bean
	public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
		cacheManager.setDefaultExpiration(1800);// 设置缓存保留时间（seconds）
		return cacheManager;
	}

	// 1.项目启动时此方法先被注册成bean被spring管理
	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {

		StringRedisTemplate template = new StringRedisTemplate(factory);
		RedisSerializer<String> redisSerializer = new StringRedisSerializer();// Long类型不可以会出现异常信息;
		template.setKeySerializer(redisSerializer);
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

}
