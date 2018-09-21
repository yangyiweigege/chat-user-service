package com.user.springboot.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chat.springboot.common.annotation.ValidateAttribute;
import com.chat.springboot.common.annotation.ValidatePage;
import com.user.springboot.domain.Person;
import com.chat.springboot.common.response.Result;
import com.chat.springboot.common.response.ResultStatus;
import com.user.springboot.service.PersonService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * <pre>
 * 功       能: 测试配置好的mongdob服务
 * 涉及版本: V3.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2018年5月14日 上午10:48:57
 * Q    Q: 2873824885
 * </pre>
 */
@RestController
@RequestMapping("/mongodb")
public class MongodbController {
	@Autowired
	private PersonService personService;

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
	 */
	@RequestMapping(value = "/find/all", method = { RequestMethod.GET, RequestMethod.POST })
	public Result<Object> findAll() {
		return personService.findAll();
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
	@ValidateAttribute(attributes = {"token"})
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "pageSize", value = "分页尺寸", required = false, dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "pageNo", value = "当前页数", required = false, dataType = "Integer", paramType = "query") })
	public Result<Object> findByPage(Integer pageSize, Integer pageNo) {
		return personService.findByPage(pageSize, pageNo);
	}
	

}
