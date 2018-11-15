package com.f4w.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


/**
 * createBy:2018-11-02 11:14:00
 *
 * @author yp
 */
@Table(name = "`busi_app`")
@Data
public class BusiApp extends BaseEntity {

    private String name;
    private String appId;
    private String appSecret;
    private Long pageId;
    private Integer status;
    private String fileName;
    private Long uid;
    private Integer auth;
}
