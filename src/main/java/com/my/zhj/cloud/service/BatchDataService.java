package com.my.zhj.cloud.service;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zhj on 18/12/20.
 */
@Service
@Slf4j
public class BatchDataService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void deleteBatchData(JobExecution job){

        long beg = System.currentTimeMillis();
        int cnt = 0;
        NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        String BATCH_JOB_INSTANCE="delete from BATCH_JOB_INSTANCE where JOB_INSTANCE_ID = ?";
        String BATCH_JOB_EXECUTION="delete from BATCH_JOB_EXECUTION where JOB_EXECUTION_ID = ?";
        String BATCH_JOB_EXECUTION_CONTEXT="delete from BATCH_JOB_EXECUTION_CONTEXT where JOB_EXECUTION_ID = ?";
        String BATCH_JOB_EXECUTION_PARAMS="delete from BATCH_JOB_EXECUTION_PARAMS where JOB_EXECUTION_ID = ?";
        String BATCH_STEP_EXECUTION_CONTEXT="delete from BATCH_STEP_EXECUTION_CONTEXT where STEP_EXECUTION_ID in (:step)";
        String BATCH_STEP_EXECUTION="delete from BATCH_STEP_EXECUTION  where STEP_EXECUTION_ID in (:step)";

        List<Long> steps = job.getStepExecutions().stream().map(StepExecution::getId).collect(Collectors.toList());

        Map<String,Object> params= Maps.newHashMap();
        params.put("step", steps);

        String instanceId = jdbcTemplate.queryForMap("select JOB_INSTANCE_ID from BATCH_JOB_EXECUTION where JOB_EXECUTION_ID = ?",
                new Object[]{job.getId()}).get("JOB_INSTANCE_ID").toString();

        cnt += jdbc.update(BATCH_STEP_EXECUTION_CONTEXT,params);
        cnt += jdbc.update(BATCH_STEP_EXECUTION,params);
        cnt += jdbcTemplate.update(BATCH_JOB_EXECUTION_CONTEXT,new Object[]{job.getId()});
        cnt += jdbcTemplate.update(BATCH_JOB_EXECUTION_PARAMS,new Object[]{job.getId()});
        cnt += jdbcTemplate.update(BATCH_JOB_EXECUTION,new Object[]{job.getId()});
        cnt += jdbcTemplate.update(BATCH_JOB_INSTANCE,new Object[]{instanceId});

        log.info("JOB_INSTANCE_ID = {}, JOB_EXECUTION_ID = {}, 删除数据成功, 删除记录:{}, 耗时:{}ms", instanceId, job.getId(), cnt, System.currentTimeMillis() - beg);
    }
}
