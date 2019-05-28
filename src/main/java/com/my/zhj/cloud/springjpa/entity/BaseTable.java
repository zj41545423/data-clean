package com.my.zhj.cloud.springjpa.entity;

import com.my.zhj.cloud.common.Constants;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID",columnDefinition = "bigint unsigned  COMMENT '主键'")
    private Long id;

    @CreatedBy
    @Column(name = "CREATED_BY",columnDefinition="varchar(32) NOT NULL DEFAULT '' COMMENT '创建人'")
    protected String createdBy =Constants.operator;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT",columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
    protected Date createdDate;

    @LastModifiedBy
    @Column(name = "UPDATED_BY",columnDefinition = "varchar(32) NOT NULL DEFAULT '' COMMENT '更新人'")
    protected String lastModifiedBy= Constants.operator;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT",columnDefinition = "datetime NOT NULL COMMENT '更新时间'")
    protected Date lastModifiedDate=new Date();
}
