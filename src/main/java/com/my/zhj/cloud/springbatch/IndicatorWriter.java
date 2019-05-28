package com.my.zhj.cloud.springbatch;

import com.my.zhj.cloud.common.TaskIndicator;
import com.my.zhj.cloud.util.IndicatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component("dataWriter")
@StepScope
public class IndicatorWriter implements ItemWriter<TaskIndicator> {

    @Value("#{jobParameters['id']}")
    private String jobId;

    @Value("#{jobExecutionContext['grade']}")
    private Integer grade;


    @Override
    public void write(List<? extends TaskIndicator> list) {

        final long beg = System.currentTimeMillis();
        ConcurrentHashMap result = (ConcurrentHashMap) IndicatorUtil.jobResult.getIfPresent(jobId);
        list.stream().filter(p -> p.getValue() != null).forEach(p -> result.put(p.getIndicatorKey(), p.getValue()));
        log.debug("write success, jobId = {}, size = {}, time = {}ms", jobId, list.size(), System.currentTimeMillis() - beg);
    }

}
