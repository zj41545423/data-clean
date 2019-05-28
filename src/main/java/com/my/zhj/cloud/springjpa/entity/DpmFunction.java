package com.my.zhj.cloud.springjpa.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dpm_functions")
public class DpmFunction extends BaseTable{

    @Column(name = "func_name",columnDefinition = "varchar(128) DEFAULT NULL COMMENT '函数名称'")
    private String funcName;
    @Column(name = "func_desc",columnDefinition = "varchar(128) DEFAULT NULL COMMENT '函数描述'")
    private String funcDesc;
    @Column(name = "func_code",columnDefinition = "text DEFAULT NULL COMMENT '函数代码'")
    private String funcCode;
    @Column(name = "func_status",columnDefinition = "varchar(20) DEFAULT NULL COMMENT '函数状态'")
    private String status;

}
