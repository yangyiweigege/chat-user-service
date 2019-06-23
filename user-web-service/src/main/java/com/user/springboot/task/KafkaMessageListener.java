package com.user.springboot.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

    @KafkaListener(topics = {"congya"}, containerFactory = "kafkaFactory")
    public void listen(ConsumerRecord<?, ?> record) {
        log.info("kafka的limitoff:{}", record.offset());
        log.info("kafka的key: " + record.key());
        log.info("kafka的value: " + record.value().toString());
        log.info("kafka的分区: " + record.partition());
    }
}
