package com.user.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: kafka
 * @Author: 杨乙伟
 * @Date Created in 2019-06-23 20:16:22
 */
@RestController
@RequestMapping("/kafka")
@Slf4j
public class KafkaController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping("/send")
    public ResponseResult<String> sendLog(String message) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", message);
            log.info("kafka的消息={}", message);
            kafkaTemplate.send("congya", "congya-key", jsonObject.toJSONString());
            log.info("发送kafka成功.");
            return new ResponseResult(ResultStatus.SUCCESS, "发送kafka成功");
        } catch (Exception e) {
            log.error("发送kafka失败", e);
            return new ResponseResult(ResultStatus.UNKNOW_ERROR, "发送kafka失败");
        }
    }
}
