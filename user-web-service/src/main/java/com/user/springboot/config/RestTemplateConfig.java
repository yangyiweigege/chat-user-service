package com.user.springboot.config;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 配置restTemplate
 * @author yangyiwei
 * @date 2018年6月4日
 * @time 下午1:46:21
 */
@Configuration
public class RestTemplateConfig {

    @Bean
   // @LoadBalanced
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        return new RestTemplate(factory);
    }

    /**
     * 开启链路追踪时所需要的bean
     * @return
     */
	@Bean
	public AlwaysSampler defaultSampler() { //服务追踪
		return new AlwaysSampler();
	}

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms
        return factory;
    }
}