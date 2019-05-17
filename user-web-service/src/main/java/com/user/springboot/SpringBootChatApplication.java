 package com.user.springboot;

 import org.springframework.boot.SpringApplication;
 import org.springframework.boot.autoconfigure.SpringBootApplication;
 import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
 import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
 import org.springframework.boot.web.servlet.ServletComponentScan;
 import org.springframework.context.annotation.ComponentScan;
 import org.springframework.core.Ordered;
 import org.springframework.scheduling.annotation.EnableScheduling;
 import org.springframework.transaction.annotation.EnableTransactionManagement;
 import tk.mybatis.spring.annotation.MapperScan;

//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
//import org.springframework.cloud.netflix.feign.EnableFeignClients;
//import org.springframework.cloud.netflix.hystrix.EnableHystrix;
//import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication(exclude = { HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class }) // spring-boot																										// 启动类
@ComponentScan(basePackages = { "com.user.springboot" }) // 基本扫包配置
@MapperScan("com.user.springboot.dao") // 配置mybatis-dao层扫描
//@EnableEurekaClient // 服务注册
@ServletComponentScan // servlete组件扫描
@EnableScheduling // 定时任务配置开启
//@EnableValidateServiceData
// @EnableDiscoveryClient//发现服务
// @EnableFeignClients//声明为feign
// @EnableHystrixDashboard//监控断路情况
// @EnableHystrix
@EnableTransactionManagement
public class SpringBootChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootChatApplication.class, args);
	}
}
