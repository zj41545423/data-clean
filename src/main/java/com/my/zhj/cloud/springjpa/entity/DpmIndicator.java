package com.my.zhj.cloud.springjpa.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "dpm_indicators" ,uniqueConstraints={
        @UniqueConstraint(name = "indicator_type_key", columnNames = {"type","ind_key"})
})
public class DpmIndicator extends BaseTable{

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
    @Column(name = "source_code", columnDefinition = "longtext COMMENT '脚本代码'")
    private String sourceCode;

    @Column(name="ind_version",columnDefinition = "int(4) DEFAULT '1' COMMENT '当前指标版本'")
    private Integer version;

    @Column(name="update_reason",columnDefinition = "varchar(255) DEFAULT NULL COMMENT '修改原因'")
    private String updateReason;


}
