package com.my.zhj.cloud.springjpa.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "dpm_functions_history", indexes = {
        @Index(name = "func_id", columnList = "func_id")
})
public class DpmFunctionHistory extends BaseTable{

    @Column(name = "func_name",columnDefinition = "varchar(128) DEFAULT NULL COMMENT '函数名称'")
    private String funcName;
    @Column(name = "func_desc",columnDefinition = "varchar(128) DEFAULT NULL COMMENT '函数描述'")
    private String funcDesc;
    @Column(name = "func_code",columnDefinition = "text DEFAULT NULL COMMENT '函数代码'")
    private String funcCode;
    @Column(name = "func_status",columnDefinition = "varchar(20) DEFAULT NULL COMMENT '函数状态'")
    private String status;
    @Column(name = "func_version", columnDefinition = "int(4) DEFAULT '1' COMMENT '函数版本信息'")
    private Integer funcVersion;

    @Column(name = "func_id", columnDefinition = "bigint(20) COMMENT '指标dpm_global_functions 表主键'")
    private Long functionsId;
    @Column(name="update_reason")
    private String updateReason;



}
