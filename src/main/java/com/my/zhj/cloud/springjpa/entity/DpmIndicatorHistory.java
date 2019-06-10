package com.my.zhj.cloud.springjpa.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "dpm_indicator_history", indexes = {
        @Index(name = "info_id", columnList = "info_id")
})
public class DpmIndicatorHistory extends BaseTable{

    @Column(name = "type", columnDefinition = "varchar(20) DEFAULT NULL COMMENT '指标类型'")
    private String type;
    @Column(name = "ind_key", columnDefinition = "varchar(128) DEFAULT NULL COMMENT '指标Key'")
    private String indicatorKey;
    @Column(name = "ind_desc", columnDefinition = "varchar(255) DEFAULT NULL COMMENT '指标描述'")
    private String indicatorDesc;
    @Column(name = "ind_grade", columnDefinition = "int(4) DEFAULT '1' COMMENT '指标处理层级'")
    private Integer grade;
    @Column(name = "ind_status", columnDefinition = "varchar(255) DEFAULT NULL COMMENT '指标状态：ON 启用；OFF 停止；HIDDEN 启用不输出'")
    private String status;
    @Column(name = "source_code", columnDefinition = "longtext COMMENT '源码'")
    private String sourceCode;

    @Column(name = "ind_version", columnDefinition = "int(4) DEFAULT '1' COMMENT '指标版本信息'")
    private Integer version;
    @Column(name="update_reason")
    private String updateReason;
    @Column(name = "info_id", columnDefinition = "bigint(20) COMMENT '指标info 表主键'")
    private Long infoId;

}
