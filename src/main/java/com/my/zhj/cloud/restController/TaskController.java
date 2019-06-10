package com.my.zhj.cloud.restController;

import com.alibaba.fastjson.JSON;
import com.my.zhj.cloud.common.Constants;
import com.my.zhj.cloud.service.IndicatorService;
import com.my.zhj.cloud.service.JobExecService;
import com.my.zhj.cloud.springjpa.entity.DpmIndicator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping(value = Constants.BASE_PATH + "/task")
@Api("指标计算API")
@Slf4j
public class TaskController {

    @Autowired
    IndicatorService indicatorService;

    @Autowired
    JobExecService jobExecService;

    @ApiOperation(value = "执行任务")
    @RequestMapping(value = "/execution/{type}", method = RequestMethod.POST)
    public Map<String, Object> runJob(@PathVariable("type") String type, @RequestParam("transId") String transId, @RequestBody Object data) throws Exception {

        log.info("接收到用户请求: transId:{}, type:{}, dataSize:{}", transId, type, JSON.toJSONString(data).length());
        if(StringUtils.isEmpty(transId)){
            transId= UUID.randomUUID().toString().replaceAll("-","");
        }
        return jobExecService.calacuteIndicator(transId, indicatorService.getIndicator(type), data);
    }


    @ApiOperation(value = "测试指定脚本")
    @RequestMapping(value = "/script/{type}/{indicator_id}", method = RequestMethod.POST)
    public Map<String, Object> testjob(@PathVariable("indicator_id") String indicatorId,
                                                  @PathVariable("type") String type,
                                                  @RequestBody Object data) throws Exception {

        //获取指定指标
        List<String> indicatorIds = Arrays.asList(indicatorId.split(","));
        List<DpmIndicator> indicators = indicatorIds.stream().map(p -> indicatorService.findIndicatorById(type, Long.valueOf(p))).collect(Collectors.toList());
        String batchId= UUID.randomUUID().toString().replaceAll("-","");
        return jobExecService.calacuteIndicator(batchId, indicators, data);
    }

}
