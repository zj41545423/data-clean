package com.my.zhj.cloud.springjpa.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "dpm_indicator_type",uniqueConstraints={
        @UniqueConstraint(name = "type_code", columnNames = {"type_code","type_code"})
})
public class DpmIndType extends BaseTable{

    @Column(name = "type_code", columnDefinition = "varchar(20) DEFAULT NULL COMMENT '指标类型'")
    private String typeCode;
    @Column(name = "type_desc", columnDefinition = "varchar(200) DEFAULT NULL COMMENT '指标类型说明'")
    private String typeDesc;

    @Column(name = "type_status", columnDefinition = "varchar(20) DEFAULT NULL COMMENT '类型状态：ON 启用；OFF 停止'")
    private String typeStatus;


}
