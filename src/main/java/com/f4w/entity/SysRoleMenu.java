package com.f4w.entity;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "`sys_role_menu`")
@Data
public class SysRoleMenu extends BaseEntity {



    private Long roleId;


    private Long menuId;

}