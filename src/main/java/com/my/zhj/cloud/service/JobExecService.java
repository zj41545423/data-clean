package com.my.zhj.cloud.service;



import com.my.zhj.cloud.springjpa.entity.DpmIndicator;
import com.my.zhj.cloud.util.ConvertUtils;
import com.my.zhj.cloud.util.IndicatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhj on 19/1/28.
 */
@Slf4j
@Service
public class JobExecService {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("dataHandleJob")
    Job job;

    @Autowired
    BatchDataService batchDataService;

    /**
     * 根据传入的指标来运算指标结果
     * @param batchId 批处理ID
     * @param indicators 指标
     * @param params 输入数据
     * @return
     */
    public Map<String, Object> calacuteIndicator(String batchId, List<DpmIndicator> indicators, Object params){


        Map<String, Object> dataIndex = new ConcurrentHashMap<>();
        IndicatorUtil.jobIndicator.put(batchId, indicators);
        IndicatorUtil.jobData.put(batchId, ConvertUtils.beanToMap(params));
        IndicatorUtil.jobResult.put(batchId,dataIndex);

        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("id", batchId);
        try {
            JobExecution run =  jobLauncher.run(job, builder.toJobParameters());
            //删除内存数据库中的数据,防止内存占用过大
            batchDataService.deleteBatchData(run);
        }catch (Exception e){
            log.info("JOB FAILED,{}",e);
        }

        //返回并过滤掉不显示的指标
        return indicators.stream().filter(p -> StringUtils.equals("ON", p.getStatus()))
                .collect(HashMap::new,(m,p) -> m.put(p.getIndicatorKey(),dataIndex.get(p.getIndicatorKey())),(m1,m2)->m1.putAll(m2));
    }


}
