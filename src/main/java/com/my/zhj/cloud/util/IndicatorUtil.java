package com.my.zhj.cloud.util;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.my.zhj.cloud.springjpa.entity.DpmIndicator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class IndicatorUtil {

    public final static Cache<String, List<DpmIndicator>> jobIndicator = CacheBuilder.newBuilder()
            .initialCapacity(100)  //设置cache的初始大小为10，要合理设置该值
            .maximumSize(1000)
            .concurrencyLevel(20)   //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
            .expireAfterWrite(30, TimeUnit.SECONDS) //设置cache中的数据在写入之后的存活时间为10秒
            .build();

    public final static Cache<String, Object> jobResult = CacheBuilder.newBuilder()
            .initialCapacity(100)  //设置cache的初始大小为10，要合理设置该值
            .maximumSize(1000)
            .concurrencyLevel(20)   //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
            .expireAfterWrite(60, TimeUnit.SECONDS) //设置cache中的数据在写入之后的存活时间为60秒
            .build();

    public final static Cache<String, Object> jobData = CacheBuilder.newBuilder()
            .initialCapacity(100)  //设置cache的初始大小为10，要合理设置该值
            .maximumSize(1000)
            .concurrencyLevel(20)   //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
            .expireAfterWrite(30, TimeUnit.SECONDS) //设置cache中的数据在写入之后的存活时间为10秒
            .build();


    @Autowired
    @Qualifier("mysqlJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    public static JdbcTemplate jdbcServer;

    @PostConstruct
    public void init(){
        jdbcServer = jdbcTemplate;
    }

    public static Date getDbTime(){
        return jdbcServer.queryForObject("select CURRENT_TIMESTAMP", Date.class);
    };


    public static String getPageContent(String url,int pageCurrent,int pageSize,int pageCount){
        if (pageCount == 0) {
            return "";
        }
        String urlNew = url.replace("{pageSize}", pageSize+"").replace("{pageCount}", pageCount+"");

        String first = urlNew.replace("{pageCurrent}", 1+"");
        String prev = urlNew.replace("{pageCurrent}", (pageCurrent - 1)+"");
        String next = urlNew.replace("{pageCurrent}", (pageCurrent + 1)+"");
        String last = urlNew.replace("{pageCurrent}", pageCount+"");

        StringBuffer html = new StringBuffer();
        html.append("<li class=\"footable-page-arrow"+(pageCurrent<=1?" disabled":"")+"\"><a href=\""+(pageCurrent<=1?"#":first)+"\">«</a></li>");
        html.append("<li class=\"footable-page-arrow"+(pageCurrent<=1?" disabled":"")+"\"><a href=\""+(pageCurrent<=1?"#":prev)+"\">‹</a></li>");
        for(int i = 0 ;i < pageCount; i++){
            String urlItem = urlNew.replace("{pageCurrent}", (i+1)+"");
            html.append("<li class=\"footable-page"+(((i+1) == pageCurrent)?" active":"")+"\"><a href=\""+urlItem+"\">"+(i+1)+"</a></li>");
        }
        html.append("<li class=\"footable-page-arrow"+(pageCurrent==pageCount?" disabled":"")+"\"><a href=\""+(pageCurrent==pageCount?"#":next)+"\">›</a></li>");
        html.append("<li class=\"footable-page-arrow"+(pageCurrent==pageCount?" disabled":"")+"\"><a href=\""+(pageCurrent==pageCount?"#":last)+"\">»</a></li>");

        return html.toString().replaceAll("null", "");
    }


}
