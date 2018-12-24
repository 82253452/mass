package com.f4w.entity;

import lombok.Data;

import javax.persistence.Table;


/**
 * createBy:2018-12-20 11:14:00
 *
 * @author yp
 */
@Table(name = "`busi_w_citys`")
@Data
public class BusiWCitys extends BaseEntity {

    Integer provinceId;
    String name;
    String cityNum;
}
