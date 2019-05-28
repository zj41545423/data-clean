package com.my.zhj.cloud.springbatch;

import com.my.zhj.cloud.util.IndicatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {

        String jobId = jobExecution.getJobParameters().getString("id");

        jobExecution.getExecutionContext().put("startTime", System.currentTimeMillis());
        jobExecution.getExecutionContext().put("grade", 1);
        jobExecution.getExecutionContext().put("data", IndicatorUtil.jobData.getIfPresent(jobId));
        jobExecution.getExecutionContext().put("indicator", IndicatorUtil.jobIndicator.getIfPresent(jobId));

        log.info("job [{}] begin, paramter:{}", jobId, jobExecution.getJobParameters());
    }


    @Override
    public void afterJob(JobExecution jobExecution) {

        long startTime = jobExecution.getExecutionContext().getLong("startTime");
        String jobId = jobExecution.getJobParameters().getString("id");
        IndicatorUtil.jobIndicator.invalidate(jobId);
        IndicatorUtil.jobData.invalidate(jobId);
        IndicatorUtil.jobResult.invalidate(jobId);

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("JOB FINISHED");
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("JOB FAILED");
        }
        log.info("Job [{}] end, STATUS: {}, Cost Time : {}ms", jobId, jobExecution.getStatus(), System.currentTimeMillis() - startTime);
    }
}
