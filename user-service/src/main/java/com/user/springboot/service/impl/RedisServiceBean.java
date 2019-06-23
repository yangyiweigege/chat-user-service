package com.user.springboot.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.user.springboot.domain.UserInfo;
import com.user.springboot.service.RedisService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

@Service
public class RedisServiceBean implements RedisService {

	@Autowired
	private JedisPool jedisPool;

	@Override
	public List<Object> execTransaction() {
		Jedis jedis = jedisPool.getResource();
		Transaction transaction = jedis.multi(); // 开启redis事务
		transaction.lpush("transaction-line", "10", "20", "30", "40", "50");
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "yangyiwei");
		map.put("age", "20");
		map.put("address", "杭州盈网科技");
		transaction.hmset("exit-employee", map);
		transaction.hmget("exit-employee", "name", "age");
		transaction.brpop(10, "transaction-line");
		List<Object> transactionResult = transaction.exec(); // 提交redis事务
		jedis.disconnect();
		return transactionResult;
	}

	@Override
	public String execPipeLined() {
		long startTime = System.currentTimeMillis();// 获取当前时间
		Jedis jedis = jedisPool.getResource();
		jedis.del("");
		Pipeline pipeline = jedis.pipelined(); // redis管道技术
		for (int i = 0; i < 1000; i++) {
			pipeline.lpush("pipe", "管道测试:" + i);
		}
		List<Object> list = pipeline.syncAndReturnAll();// 发送redis管道
		System.out.println(list.toString());
		jedis.disconnect();
		long endTime = System.currentTimeMillis();
		return "程序运行时间:" + (endTime - startTime);
	}

	@Override
	public JSONObject hashOperate(JSONObject jsonObject) {
		Jedis jedis = jedisPool.getResource();
		Map<String, String> dataMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
			System.out.println("key : " + entry.getKey() + " value :" + entry.getValue());
			dataMap.put(entry.getKey(), entry.getValue() + "");
		}
		jedis.hmset("spring-boot-hash", dataMap);
		jedis.hset("spring-boot-hash", "detail", jsonObject.toJSONString());
		List<String> list = jedis.hmget("spring-boot-hash", "name", "age", "detail");
		System.out.println("hmse返回的结果： " + list.toString());
		Map<String, String> redisHash = jedis.hgetAll("spring-boot-hash");
		for (Map.Entry<String, String> entry : redisHash.entrySet()) {
			System.out.println("redis中key : " + entry.getKey() + " value :" + entry.getValue());
			dataMap.put(entry.getKey(), entry.getValue() + "");
		}
		return jsonObject;
	}

	@Override
	public String highKill() {
		Jedis jedis = jedisPool.getResource();
		jedis.select(15);
		long num = jedis.incr("num");
		String buyResult = "";
		if (num < 20) {
			jedis.lpush("result_queen", num + "" + "线程名:" + Thread.currentThread().getName());
			buyResult = "购买成功了";
		} else {
			buyResult = "购买失败了";
		}
		jedis.close();
		return buyResult;
	}

	@Override
	public String distributeLock() {
		Jedis jedis = jedisPool.getResource();
		jedis.select(15);
		// 如果
		jedis.set("a","a","NX","PX",10);
		while (true) { // 没有锁 则一直等待 模拟分布式锁
			if (jedis.setnx("lock", "1") == 1) {// 如果获得锁，则执行
				jedis.expire("lock", 20);
				break;
			} else {
				try {
					System.out.println("没有获得锁，等待了，，，老铁");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(Thread.currentThread().getName() + " 获得了锁 并且顺利执行~~~~~~");
		jedis.del("lock");
		jedis.disconnect();
		return "执行完毕";
	}

	@Override
	public boolean publishMessage(String message) {
		Jedis jedis = jedisPool.getResource();
		jedis.publish("message", message);
		jedis.disconnect();
		return true;
	}

	@Override
	public String writeUserInfo(UserInfo userInfo) {
		Jedis jedis = jedisPool.getResource();
		String token = UUID.randomUUID().toString();
		jedis.set(token, JSONObject.toJSONString(userInfo));
		jedis.expire(token, 3600);//设置过期时间1小时
		jedis.close();
		return token;
	}

	@Override
	public UserInfo getUserInfoByToken(String token) {
		Jedis jedis = jedisPool.getResource();
		String info = jedis.get(token);
		jedis.close();
		if (info != null) {
			return JSONObject.parseObject(info, UserInfo.class);
		}
		return null;
	}

}
