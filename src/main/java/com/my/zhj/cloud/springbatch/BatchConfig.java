package com.my.zhj.cloud.springbatch;

import com.my.zhj.cloud.common.TaskIndicator;
import com.my.zhj.cloud.springjpa.entity.DpmIndicator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.Resource;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Resource
    private JobBuilderFactory jobBuilderFactory;    //用于构建JOB

    @Resource
    private StepBuilderFactory stepBuilderFactory;  //用于构建Step

    @Resource
    private JobListener jobListener;            //简单的JOB listener

    @Autowired
    private TaskDecider taskDecider;

    @Autowired
    @Qualifier("dataReader")
    private ItemReader dataReader;

    @Autowired
    @Qualifier("dataWriter")
    private ItemWriter dataWriter;

    @Autowired
    @Qualifier("dataProcessor")
    private ItemProcessor dataProcessor;

    @Autowired
    @Qualifier("taskPool")
    private TaskExecutor dataTask;


    /**
     * 一个简单基础的Job通常由一个或者多个Step组成
     */
    @Bean
    public Job dataHandleJob() {
        return jobBuilderFactory.get("dataHandleJob")
                .incrementer(new RunIdIncrementer())
                .start(handleDataStep())    //start是JOB执行的第一个step
                .next(taskDecider)
                .from(taskDecider).on("COMPLETED").end()
                .from(taskDecider).on("CONTINUE").to(handleDataStep())
                .end()
                .listener(jobListener)      //设置 JobListener
                .build();
    }


    /**
     * 一个简单基础的Step主要分为三个部分
     * ItemReader : 用于读取数据
     * ItemProcessor : 用于处理数据
     * ItemWriter : 用于写数据
     */
    public Step handleDataStep() {
        return stepBuilderFactory.get("dealData").
                <DpmIndicator, TaskIndicator>chunk(10).        // <输入,输出> 。chunk通俗的讲类似于SQL的commit; 这里表示处理(processor)100条后写入(writer)一次。
                faultTolerant().retryLimit(1).retry(Exception.class)
                .skipLimit(1).skip(Exception.class). //捕捉到异常就重试,重试100次还是异常,JOB就停止并标志失败
                reader(dataReader).         //指定ItemReader
                processor(dataProcessor).   //指定ItemProcessor
                writer(dataWriter).         //指定ItemWriter
                taskExecutor(dataTask).
                build();
    }

}
