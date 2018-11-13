package com.f4w.entity;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "`sys_role`")
@Data
public class SysRole extends BaseEntity {

    private String roleName;

    private String remark;


    private Long createUserId;


    private Date createTime;

}