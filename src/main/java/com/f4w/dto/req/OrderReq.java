package com.f4w.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author yp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReq {
    private Integer carTypeId;
    private String sendAddress;
    private String receiveAddress;
    private String des;
    private BigDecimal amount;
    private String[] time;
    private String[] addressRoute;
    private Integer discharge;
    private Integer driverTop;
    private Address addressFrom;
    private Address addressTo;
    private String remark;
    private String carTypeName;
}

