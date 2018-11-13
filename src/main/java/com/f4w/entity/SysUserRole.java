package com.f4w.entity;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "`sys_user_role`")
@Data
public class SysUserRole extends BaseEntity {

    private Long roleId;


    private Long userId;



}