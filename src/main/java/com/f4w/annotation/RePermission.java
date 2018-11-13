package com.f4w.annotation;

import java.lang.annotation.*;

/**
 * @author blueriver
 * @description 与拦截器结合使用 验证权限
 * @date 2017/11/17
 * @since 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RePermission {
    String value();
}
