package com.f4w.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DingWarning {
    public static void log(String msg, String... arg) {
        log.error("-------钉钉报警错误-------" + msg, arg);
    }
}
