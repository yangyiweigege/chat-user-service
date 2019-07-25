package com.user.springboot.annotation;

import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.annotation.ValidateRequestBody;
import com.chat.springboot.common.annotation.ValidateServiceData;
import com.user.springboot.controller.CheckParam;
import com.user.springboot.domain.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用来验证 自定义注解  是否生效
 */
@RequestMapping("/annotation")
@RestController
public class AnnotationController {



    @RequestMapping("/global/lock")
    @GlobalLock(key = "#name")
    public String testDLock(String name) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello world";
    }

    @RequestMapping("/global/another")
    @GlobalLock(path = "yangyiwei", key = "#user.userName", waitTime = 2, leaseTime = 120)
    public String testDLock(User user) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello world";
    }

    @RequestMapping("/repeat/submit")
    @RepeatSubmitLimiter
    public String tesRepeatSubmit(String name) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello world";
    }

    @RequestMapping("/validate/json")
    @ValidateRequestBody(attributes = {"name", "age"})
    public String testValidateJSON(@RequestBody JSONObject jsonObject) {

        return "hello world";
    }

    @RequestMapping("/validate/data")
    @ValidateServiceData(openCheckParam = true)
    public String testValidateJSON(@CheckParam String age, @CheckParam String name) {

        return "hello world";
    }


}
