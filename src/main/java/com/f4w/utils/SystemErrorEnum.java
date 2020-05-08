package com.f4w.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum SystemErrorEnum {

    /**
     *
     */
    NOT_FOUND_INDEX(1001, "id 不存在"),
    USER_ERROR(1002,"用户名错误"),
    USER_EXP(100, "登录失效"),
    HOUR(4, "小时");

    private final Integer code;
    private final String description;
}
