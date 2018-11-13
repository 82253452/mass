package com.f4w.entity;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "`sys_menu`")
@Data
public class SysMenu extends BaseEntity {

    private Long parentId;

    private String name;


    private String url;

    private String perms;


    private Integer type;

    private String icon;


    private Integer orderNum;

}