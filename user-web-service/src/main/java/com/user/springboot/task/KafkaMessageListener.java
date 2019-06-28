package com.user.springboot.task;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @Description: 监听消息
 * @Author: 杨乙伟
 * @Date Created in 2019-06-23 20:21:29
 */
@Slf4j
@Component
public class KafkaMessageListener {

    @Autowired
    private MongoTemplate mongoTemplate;

    @KafkaListener(topics = {"congya"}, containerFactory = "kafkaFactory")
    public void listen(ConsumerRecord<?, ?> record) {
        log.info("kafka的limitoff:{}", record.offset());
        log.info("kafka的key: " + record.key());
        log.info("kafka的value: " + record.value().toString());
        log.info("kafka的分区: " + record.partition());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("limit", record.offset());
        jsonObject.put("key", record.key());
        jsonObject.put("partition", record.partition());
        jsonObject.put("message", record.value().toString());
        mongoTemplate.insert(jsonObject, "imooc");
    }
}
