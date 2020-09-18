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
    private String userName;
    private String phone;
    private String sendAddress;
    private String receiveAddress;
    private Double latitudeFrom;
    private Double latitudeTo;
    private Double longitudeFrom;
    private Double longitudeTo;
    private String addressFrom;
    private String addressTo;
    private String des;
    private BigDecimal amount;
}

