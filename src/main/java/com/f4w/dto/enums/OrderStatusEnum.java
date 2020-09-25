package com.f4w.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author admin
 */
@Getter
@ToString
@RequiredArgsConstructor
public enum OrderStatusEnum {
    INIT(0, "系统用户"),
    TRANS_COMPANY1(1, "物流公司"),
    TRANS_COMPANY2(2, "司机"),
    TRANS_COMPANY3(3, "司机"),
    TRANS_COMPANY4(4, "司机"),
    TRANS_COMPANY5(5, "司机"),
    TRANS_COMPANY6(6, "司机"),
    TRANS_COMPANY7(7, "司机");

    private final Integer code;
    private final String description;
}
