package com.my.zhj.cloud.springbatch;

import com.my.zhj.cloud.springjpa.entity.DpmIndicator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("taskDecider")
public class TaskDecider implements JobExecutionDecider {

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

        //判断是否含有下级指标需要处理
        int grade = jobExecution.getExecutionContext().getInt("grade") + 1;
        List<DpmIndicator> indicators = (List<DpmIndicator>)jobExecution.getExecutionContext().get("indicator");

        long cnt = 0;
        if(indicators!=null) {
            cnt = indicators.stream().filter(p -> p.getGrade().equals(grade)).count();
        }

        if (cnt == 0) {
            return new FlowExecutionStatus("COMPLETED");
        } else {
            jobExecution.getExecutionContext().putInt("grade", grade);
            return new FlowExecutionStatus("CONTINUE");
        }
    }

}
