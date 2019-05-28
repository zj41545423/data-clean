package com.my.zhj.cloud.util;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import java.util.Map;

/**
 * Created by zhj on 2018/10/30.
 */
@Slf4j
public class ConvertUtils {
    public static Map<String, Object> beanToMap(Object obj) {
        Preconditions.checkArgument(obj != null, "对象不能为空");
        if(obj instanceof Map) {
            return (Map)obj;
        } else {
            try {
                Map<String, Object> map = PropertyUtils.describe(obj);
                map.remove("class");
                return map;
            } catch (Throwable var2) {
                log.error("将对象[{}]转换为map异常: {}", JSON.toJSONString(obj), ExceptionUtils.getStackTrace(var2));
                throw new RuntimeException(String.format("将对象[%s]转换为map异常", new Object[]{JSON.toJSONString(obj)}));
            }
        }
    }
}
