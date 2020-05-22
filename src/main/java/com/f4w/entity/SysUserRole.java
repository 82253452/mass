package com.f4w.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Table(name = "`sys_user_role`")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserRole extends BaseEntity {

    private Long roleId;
    private Long userId;

}