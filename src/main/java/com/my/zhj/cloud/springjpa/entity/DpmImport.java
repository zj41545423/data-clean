package com.my.zhj.cloud.springjpa.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "dpm_imports")
public class DpmImport extends BaseTable{

    @Column(name = "imports",columnDefinition = "varchar(128) DEFAULT NULL COMMENT '导入类信息'")
    private String imports;

}
