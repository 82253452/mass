package com.f4w.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


/**
 * createBy:2018-11-02 11:14:00
 *
 * @author yp
 */
@Table(name = "`busi_app_page`")
@Data
public class BusiAppPage extends BaseEntity {

    private String pageName;
    private String appId;
    private String appSecret;
    private String content;
    private Long uid;
    private Integer custom;
}
