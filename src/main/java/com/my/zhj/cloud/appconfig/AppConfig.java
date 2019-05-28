package com.my.zhj.cloud.appconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AppConfig {

    /** 初始线程处理数 默认取CPU线程数*2 ,建议不配置 */
    @Value("${data.clean.core.size:#{T(java.lang.Runtime).getRuntime().availableProcessors() *2}}")
    private int coreSize;
    /** 最大线程数 */
    @Value("${data.clean.max.size:#{T(java.lang.Runtime).getRuntime().availableProcessors() *4}}")
    private int maxSize ;

    /** 最大队列数 */
    @Value("${data.clean.queue.size:#{T(java.lang.Runtime).getRuntime().availableProcessors() *3}}")
    private int queueSize;

    @Bean(name = "taskPool")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(coreSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxSize);
        threadPoolTaskExecutor.setQueueCapacity(queueSize);
        threadPoolTaskExecutor.setThreadNamePrefix("Data-Job-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.my.zhj.cloud.controller"))
                .paths(PathSelectors.ant("/**"))
                .build()
                .apiInfo(ApiInfo.DEFAULT)
                .enable(true);
    }
}
