package com.user.springboot.dao;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.springboot.domain.Person;
/**
 * person的数据层
 * @author yangyiwei
 * @date 2018年6月22日
 * @time 上午10:34:05
 */
public interface PersonDao extends  MongoRepository<Person, Integer>{
	
	//@Cacheable(value = "person", keyGenerator = "keyGenerator") 此方法可以采用自定义生成策略 不建议使用
	@Cacheable(value="user", key="'users_'+#p0")
	public Person findOne(Integer id); 
	
	 /** 
     * 新增或修改时 
     */  
    @CachePut(value = "user", key = "'users_'+#p0.id")  
	public Person insert(Person person);
    
    /**
     * 删除key的时候
     */
    @CacheEvict(value="user", key="'users_'+#p0") 
    public void delete(Integer id);

    /**
     * 根据id修改不为空属性
     * @param person
     */
	public int updateExistDataById(Person person);

	/**
	 * 分页查询person表
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public List<Person> findPage(Integer pageSize, Integer pageNo);
}
