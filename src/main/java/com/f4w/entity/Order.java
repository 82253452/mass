package com.f4w.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Table;

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
    private Integer receiveUserId;
    private Integer productId;
    private String originalPrice;
    private String amount;
    private String des;
    private Integer status;
}
