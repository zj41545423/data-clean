package com.my.zhj.cloud.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskIndicator {
    private String indicatorKey;
    private String type;
    private Integer grader;
    private Object value;
}
