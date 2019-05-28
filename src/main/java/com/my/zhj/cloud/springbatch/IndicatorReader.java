package com.my.zhj.cloud.springbatch;

import com.my.zhj.cloud.springjpa.entity.DpmIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Vector;

@Configuration
@StepScope
public class IndicatorReader {

    private static final Logger log = LoggerFactory.getLogger(IndicatorReader.class);

    @Value("#{jobExecutionContext['indicator']}")
    private List<DpmIndicator> indicators;

    @Value("#{jobExecutionContext['grade']}")
    private Integer grade;


    @Bean(name = "dataReader")
    @StepScope
    public ItemReader<DpmIndicator> getDataReader() {

        Vector<DpmIndicator> gradeIndicator = new Vector<>();
        if(indicators != null) {
            indicators.stream().filter(p -> grade.equals(p.getGrade()))
                    .forEach(p->gradeIndicator.add(p));
        }
        log.debug("READ_{}-->读取指标数量 : size:{}", grade, gradeIndicator.size());


        return new DpmReader(gradeIndicator);
    }

}
