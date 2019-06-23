package com.user.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.common.annotation.CheckPageBean;
import com.chat.springboot.common.annotation.ValidateAttribute;
import com.chat.springboot.common.excel.ExcelUtil;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.Result;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.domain.User;
import com.user.springboot.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/mybatis")
@Api(value = "mybatis-controller", description = "mybatis控制层")
@Slf4j
public class MyBatisController {

    @Autowired
    private UserService userService;
    @Value("${server.port}")
    private String port;
    @Autowired
    @Qualifier("threadPoolExecutor")
    private ExecutorService executorService;



    @RequestMapping(value = "/batchUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult<?> batchUpdate() {

        userService.batchUpdate();

        return new ResponseResult<JSONObject>(ResultStatus.SUCCESS);
    }


    /**
     * 测试嵌套事务 比如serviceA中 调用a方法 a方法继续调用b方法 看能否回滚 的确可以回滚
     *
     * @return
     */
    @RequestMapping(value = "/transaction", method = {RequestMethod.GET, RequestMethod.POST})
    @ValidateAttribute(attributes = {"userName"})
    public ResponseResult<?> testInnerTransaction(User user) {

        return new ResponseResult<>();
    }

    @RequestMapping(value = "/save", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult<?> saveUser(User user) throws Exception {
        return userService.save(user);
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @CheckPageBean(currentPage = "currentPage")
    public Result<List<User>> userList(String currentPage) {
        List<User> list = userService.findList(Integer.parseInt(currentPage));
        return new Result<List<User>>(ResultStatus.SUCCESS, list);
    }

    @RequestMapping(value = "/list/export/{currentPage}", method = {RequestMethod.GET, RequestMethod.POST})
    public void exportUserList(@PathVariable("currentPage") Integer currentPage, HttpServletRequest request,
                               HttpServletResponse response) {
        List<User> list = userService.findList(currentPage);
        ExcelUtil.exportExcel(list, "出错记录", "出错记录", User.class, "出错记录.xls", response, request);
    }

    @RequestMapping(value = "/find/attribute", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<List<User>> findByAttribute(String user) {
        return userService.findByAttribute(user);
    }

    /**
     * <pre>
     * 功       能: 使用了缓存
     * 涉及版本: V3.0.0
     * 创  建  者: yangyiwei
     * 日       期: 2018年5月3日 下午3:19:11
     * Q    Q: 2873824885
     * </pre>
     */
    @RequestMapping(value = "/find/detail/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<Object> findById(@PathVariable("id") String id) {
        Result<Object> result = new Result<Object>();
        return result.setCode(ResultStatus.SUCCESS).setData(userService.findById(id));
    }

    @RequestMapping(value = "/find/name/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<Object> findByName(User user, @PathVariable("name") String name) {
        int i = 1 / 0;
        Result<Object> result = userService.findByName(name);
        return result;
    }

    @RequestMapping(value = "/insertAnother", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<Object> finsertAnother() {
        return new Result<>(userService.insertAnotherOne());
    }


    @RequestMapping(value = "/batch/task", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<Object> batchTask() throws Exception {
        ArrayBlockingQueue<Integer> arrayBlockingQueue = new ArrayBlockingQueue<Integer>(100); //模拟100个任务
        for (int i = 0; i < 10; i++) {
            arrayBlockingQueue.add(i);
        }
        Future<?> future1 = executorService.submit(() -> {
            while (!arrayBlockingQueue.isEmpty()) {
                try {
                    arrayBlockingQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Future<?> future2 = executorService.submit(() -> {
            while (!arrayBlockingQueue.isEmpty()) {
                try {
                    arrayBlockingQueue.take(); //拿出任务 并且处理
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        future1.get();
        future2.get();
        return new Result<>(ResultStatus.SUCCESS);
    }



}
