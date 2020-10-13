package com.f4w.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: yp
 * @Date: 2020/9/8 15:28
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`order`")
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {
    private String orderNo;
    private Integer orderType;
    private Integer userId;
    private Integer transId;
    private Integer receiveUserId;
    private Integer productId;
    private BigDecimal originalPrice;
    private BigDecimal amount;
    private String des;
    private Integer status;
    private Double latitudeFrom;
    private Double latitudeTo;
    private Double longitudeFrom;
    private Double longitudeTo;
    private String addressFrom;
    private String addressTo;
    private String userName;
    private String phone;
    private String addressRoute;
    private String addressFromDetail;
    private String addressToDetail;
    private String remark;
    private Date deliveryTimeStart;
    private Date deliveryTimeEnd;
    private Integer discharge;
    private Integer driverTop;
    private String addressFromHome;
    private String addressToHome;
    private String receivePhone;
    private String receiveName;
    private String productName;
    private String addressProvinceFrom;
    private String addressCityFrom;
    private String addressDistrictFrom;
    private String addressProvinceTo;
    private String addressCityTo;
    private String addressDistrictTo;
}
