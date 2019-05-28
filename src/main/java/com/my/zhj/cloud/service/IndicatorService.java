package com.my.zhj.cloud.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.my.zhj.cloud.springjpa.entity.*;
import com.my.zhj.cloud.springjpa.entity.QDpmIndicator;
import com.my.zhj.cloud.springjpa.repository.DpmFunctionRepository;
import com.my.zhj.cloud.springjpa.repository.DpmImportRepository;
import com.my.zhj.cloud.springjpa.repository.DpmIndicatorRepository;
import com.my.zhj.cloud.util.IndicatorUtil;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IndicatorService {

    private Map<String, Map<Long, DpmIndicator>> indicators = Maps.newHashMap();

    private Map<String, GroovyObject> groovyObjectMap = Maps.newHashMap();

    private ImmutableList<String> indStatus= ImmutableList.of("ON","HIDDEN");

    @Autowired
    private DpmIndicatorRepository dpmIndicatorRepository;
    @Autowired
    private DpmImportRepository dpmImportRepository;
    @Autowired
    private DpmFunctionRepository dpmFunctionRepository;

    @PostConstruct
    public void init() throws Exception {
        //加载所有的指标
        Iterable<DpmIndicator> allIndicator = dpmIndicatorRepository.findAll(QDpmIndicator.dpmIndicator.status.in(indStatus));

        for (DpmIndicator in : allIndicator) {
            if (!indicators.containsKey(in.getType())) {
                indicators.put(in.getType(), Maps.newConcurrentMap());
            }
            indicators.get(in.getType()).put(in.getId(), in);
        }
        for (String s : indicators.keySet()) {
            log.info("指标加工程序加载指标: type {} ,加载数量:{}", s, indicators.get(s).size());
        }

        //加载脚本, 一种 type 生成一个脚本
        for (String type : indicators.keySet()) {
            long beg = System.currentTimeMillis();
            groovyObjectMap.put(type,generateGroovyByType(type, indicators.get(type).values()));
            log.info("指标 :{} 处理脚本加载完成,共加载处理脚本数量 {},耗时 :{} ms", type, indicators.get(type).size(), System.currentTimeMillis() - beg);
        }
    }

    /**
     * 每一分钟更新下groovy脚本库
     */
    @Scheduled(fixedDelay = 1000 * 60)
    public void updateGroovy(){
        Date timeLine = DateUtils.addSeconds(IndicatorUtil.getDbTime(), -60);
        List<DpmIndicator> incriIndicator = Lists.newArrayList(dpmIndicatorRepository.findAll(QDpmIndicator.dpmIndicator.lastModifiedDate.goe(timeLine)));
        //先更新指标配置信息  indicators  Map<String, Map<Long, DpmIndicator>>
        for (DpmIndicator p : incriIndicator) {
            if (!indicators.containsKey(p.getType())) {
                indicators.put(p.getType(), Maps.newConcurrentMap());
            }
            if(indStatus.contains(p.getStatus())){
                indicators.get(p.getType()).put(p.getId(),p);
            }else if(StringUtils.equals(p.getStatus(),"OFF")){
                indicators.get(p.getType()).remove(p.getId());
            }
        }

        //更新指标脚本
        Set<String> types = incriIndicator.stream().map(DpmIndicator::getType).collect(Collectors.toSet());
        for (String type : types) {
            long beg = System.currentTimeMillis();
            try {
                groovyObjectMap.put(type, generateGroovyByType(type, indicators.get(type).values()));
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("指标 :{} 处理脚本加载完成,共加载处理脚本数量 {},耗时 :{} ms", type, indicators.get(type).size(), System.currentTimeMillis() - beg);
        }
        log.info("groovy 脚本库更新完成, 共更新指标数量:{},更新时间戳:{}", incriIndicator.size(), DateFormatUtils.format(timeLine,"yyyy-MM-dd HH:mm:ss"));

    }


    private StringBuilder getScriptHead() {
        //加载计算脚本
        List<DpmImport> imports = dpmImportRepository.findAll();
        Iterable<DpmFunction> functions = dpmFunctionRepository.findAll(com.my.zhj.cloud.springjpa.entity.QDpmFunction.dpmFunction.status.eq("ON"));

        StringBuilder result = new StringBuilder();

        imports.forEach(p -> result.append(gImport(p)));
        functions.forEach(p -> result.append(gClosure(p)));

        return result;
    }

    public List<DpmIndicator> getIndicator(String type) {
        if (indicators.containsKey(type)) {
            return indicators.get(type).values().stream().collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    public DpmIndicator findIndicatorById(String type, Long id) {

        if(!indicators.containsKey(type)){
            return null;
        }
        return indicators.get(type).get(id);
    }

    public GroovyObject generateGroovyByType(String type, Collection<DpmIndicator> indicators) throws Exception{
        StringBuilder tempScript = getScriptHead();
        indicators.forEach(p -> tempScript.append(gIndicator(p)));
        GroovyObject groovy;
        GroovyClassLoader classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
        try {
            log.info("try to parse script: \n {}",tempScript.toString());
            Class groovyClass = classLoader.parseClass(tempScript.toString(), "script_" + type);
            groovy = (GroovyObject) groovyClass.newInstance();
        } catch (Exception e) {
            log.error("脚本加载出错:脚本名称:{},脚本代码: \n {}", "script_" + type, tempScript.toString(), e);
            throw e;
        }
        return groovy;
    }


    public GroovyObject getGroovy(String type) {
        return groovyObjectMap.get(type);
    }

    private String gClosure(DpmFunction func){
        return String.format("def %s %s  ;\n",func.getFuncName(),func.getFuncCode());
    }

    private String gImport(DpmImport dImport){
        return String.format("import %s ;\n",dImport.getImports());
    }

    private String gIndicator(DpmIndicator indicator){
        return String.format("public Object %s_%s(Map<String, Object> data, Map<String, Object> res){ \n %s \n};\n"
                ,indicator.getType(),indicator.getIndicatorKey(),indicator.getSourceCode());
    }

}
