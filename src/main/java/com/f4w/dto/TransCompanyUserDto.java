package com.f4w.dto;

import com.f4w.entity.SysUser;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @Author: yp
 * @Date: 2020/9/17 16:16
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransCompanyUserDto extends SysUser {
    private Integer status;
}
