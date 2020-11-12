package com.f4w.dto.req;

import com.f4w.dto.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author yp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReq {
    @NotNull(message = "请选择车型")
    private Integer carTypeId;
    private String sendAddress;
    @NotBlank
    private String receiveAddress;
    private String des;
    private BigDecimal amount;
    private String[] time;
    private Location[] addressRoute;
    private Integer discharge;
    private Integer driverTop;
    @NotNull(message = "请选择发货地址")
    private Address addressFrom;
    @NotNull(message = "请选择收货地址")
    private Address addressTo;
    private String remark;
    private String carTypeName;
}

