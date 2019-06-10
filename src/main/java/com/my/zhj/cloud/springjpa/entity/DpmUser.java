package com.my.zhj.cloud.springjpa.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zjy on 2018/11/16.
 */
@Data
@Entity
@Table(name = "dpm_user", indexes = {
        @Index(name = "idx_username", columnList = "user_name"),
})
public class DpmUser extends BaseTable {

    @Column(name = "user_name", columnDefinition = "varchar(128) COMMENT '用户名'")
    private String userName;
    @Column(name = "user_passwd", columnDefinition = "varchar(128) DEFAULT NULL COMMENT '用户密码'")
    private String password;
    @Column(name = "user_type", columnDefinition = "varchar(256) DEFAULT NULL COMMENT '用户管理指标列表'")
    private String userType;
    //用户操作等级 0 级才有修改和删除的权限,1是没有
    @Column(name = "user_level", columnDefinition = "int DEFAULT NULL COMMENT '用户管理指标列表,0 系统等级,1 可操作全局用户 ,2 普通用户'")
    private Integer lev;
}
