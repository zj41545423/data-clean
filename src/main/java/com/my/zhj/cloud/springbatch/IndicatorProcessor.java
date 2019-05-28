package com.my.zhj.cloud.springbatch;

import com.alibaba.fastjson.JSON;
import com.my.zhj.cloud.common.TaskIndicator;
import com.my.zhj.cloud.service.IndicatorService;
import com.my.zhj.cloud.springjpa.entity.DpmIndicator;
import com.my.zhj.cloud.util.IndicatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@StepScope
@Component("dataProcessor")
public class IndicatorProcessor implements ItemProcessor<DpmIndicator, TaskIndicator> {

    @Value("#{jobParameters['id']}")
    private String jobId;


    @Value("#{jobExecutionContext['data']}")
    private Map<String, Object> params;

    @Value("#{jobExecutionContext['grade']}")
    private Integer grade;

    @Autowired
    private IndicatorService indicatorService;


    @Override
    public TaskIndicator process(DpmIndicator indicator) {
        Map indicators = (Map) IndicatorUtil.jobResult.getIfPresent(jobId);

        Object value = null;
        long beg=System.currentTimeMillis();
        try {
            value = indicatorService.getGroovy(indicator.getType()).invokeMethod(indicator.getType()+"_"+indicator.getIndicatorKey(), new Object[]{params,indicators});
        } catch (Exception e) {
            log.error("jobId = {}, 处理脚本{} ,发生异常,error:{}", jobId, indicator.getIndicatorKey(), e);
        }
        long cost=System.currentTimeMillis()-beg;
        if(cost > 100){
            String resJson = JSON.toJSONString(value);
            String result = (resJson != null && resJson.length() > 100) ? String.format("dataSize[%d]", resJson.length()) : resJson;
            log.warn("jobId = {}, 处理脚本{}, 耗时:{}ms, 脚本结果:{}", jobId, indicator.getIndicatorKey(), cost, result);
        }
        log.debug("jobId = {}, 处理脚本{}, 耗时:{}ms, 脚本结果:{}", jobId, indicator.getIndicatorKey(), cost, JSON.toJSONString(value));
        return TaskIndicator.builder()
                .indicatorKey(indicator.getIndicatorKey())
                .grader(indicator.getGrade())
                .type(indicator.getType())
                .value(value)
                .build();
    }
}
