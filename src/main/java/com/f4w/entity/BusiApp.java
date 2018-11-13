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

    /**
     * 名称
     */
    private String name;
    /**
     * app_id
     */
    private String appId;
    /**
     * app_secret
     */
    private String appSecret;
    private Long pageId;
    private Integer status;
    private String fileName;
    private Long uid;
}
