package com.user.springboot.controller;
import com.chat.springboot.common.PageBean;
import com.chat.springboot.common.annotation.ValidateAttribute;
import com.chat.springboot.common.annotation.ValidatePage;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.Result;
import com.chat.springboot.common.response.ResultStatus;
import com.mongodb.BasicDBObject;
import com.user.springboot.domain.Person;
import com.user.springboot.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * <pre>
 * 功       能: 测试配置好的mongdob服务
 * 涉及版本: V3.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2018年5月14日 上午10:48:57
 * Q    Q: 2873824885
 * </pre>
 */
@Api(value = "mongodb-controoler", description = "mongodb控制层")
@RestController
@RequestMapping("/mongodb")
@Slf4j
public class MongodbController {
	@Autowired
	private PersonService personService;
	@Autowired
	private MongoTemplate mongoTemplate;
    @Autowired
    @Qualifier("threadPoolExecutor")
    private ExecutorService executorService;
  /*  @Autowired
    @Qualifier("traceThreadPoolExecutor")
	private ExecutorService treaceThreadPoolExecutor;*/
	private Semaphore semaphore = new Semaphore(10);

	/**
	 * 新增一个人
	 * 
	 * @param person
	 * @return
	 */
	@RequestMapping(value = "/insert", method = { RequestMethod.POST, RequestMethod.GET })
	@ApiOperation(value = "新增一个人")
	@ApiImplicitParam(name = "person", value = "人员信息", required = true, dataType = "person", paramType = "body")
	public Result<Object> insert(Person person) {
		return personService.insert(person);
	}

	/**
	 * 删除一个人
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.GET })
	@ApiOperation(value = "删除一个人")
	@ApiImplicitParam(name = "id", value = "人员id", required = true, dataType = "Integer")
	public Result<Object> delete(@PathVariable("id") Integer id) {
		return personService.delete(id);
	}

	/**
	 * 根据id更新
	 * 
	 * @param person
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	@ApiOperation(value = "更新一个人")
	@ApiImplicitParam(name = "person", value = "人员信息", required = true, dataType = "Integer")
	public Result<Object> update(@Valid Person person, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new Result<Object>().setCode(ResultStatus.LACK_PARAM)
					.setMessage(bindingResult.getFieldError().getDefaultMessage());
		}
		Result<Object> result = personService.update(person);
		return result;
	}

	/**
	 * 返回所有人员
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@RequestMapping(value = "/find/all", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseResult<List<Person>> findAll() throws InterruptedException, ExecutionException {
		try {
			semaphore.acquire(); // 限制接口最大访问数10
			CountDownLatch countDownLatch = new CountDownLatch(2);
			Future<List<Person>> list1 = executorService.submit(() -> {
				List<Person> persons = personService.findAll();
				countDownLatch.countDown();
				return persons;
			});
			Future<List<Person>> list2 = executorService.submit(() -> {
				List<Person> persons = personService.findAll();
				countDownLatch.countDown();
				return persons;
			});
			countDownLatch.await(2000, TimeUnit.MILLISECONDS); // 两个任务做完往下执行,最好设置超时时间
			List<Person> persons = new ArrayList<>();
			persons.addAll(list1.get());
			persons.addAll(list2.get());
			
			return new ResponseResult<>(ResultStatus.SUCCESS, persons);
		} finally {
			semaphore.release();
		}
		
	}

	/**
	 * 根据id查询
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/find/detail/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public Result<Object> findById(@PathVariable("id") Integer id) {
		return personService.findById(id);
	}

	/**
	 * 分页查询
	 * 
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@RequestMapping(value = "/find/page", method = RequestMethod.GET)
	@ValidatePage
	@ApiOperation(value = "分页查询")
	@ValidateAttribute(attributes = { "token" })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "pageSize", value = "分页尺寸", required = false, dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "pageNo", value = "当前页数", required = false, dataType = "Integer", paramType = "query") })
	public Result<PageBean<Person>> findByPage(Integer pageSize, Integer pageNo) {
		return personService.findByPage(pageSize, pageNo);
	}

	@RequestMapping(value = "/template")
	public ResponseResult<?> mongoDBTemplateOid() {
		int i = 1 / 0;
		Query query = new Query();
		query.addCriteria(new Criteria().and("_id").is("5b7ea3a93713660080efc229"));
		List<BasicDBObject> bsonObject = mongoTemplate.find(query, BasicDBObject.class, "log");
		System.out.println(bsonObject);
		return new ResponseResult<>(ResultStatus.SUCCESS, bsonObject);
	}
	
	@RequestMapping("/string")
	public ResponseResult<String> returnString() {
		return new ResponseResult<>(ResultStatus.SUCCESS, "hello world");
	}
	
	@RequestMapping("/slow")
	public ResponseResult<String> slow() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseResult<>(ResultStatus.SUCCESS, "hello world");
	}
}
