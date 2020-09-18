package com.f4w.dto;

import com.f4w.entity.Company;
import com.f4w.entity.SysUser;
import com.f4w.entity.TransCompany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author admin
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SysUserDto extends SysUser {
    private String token;
    private List<TransCompany> transCompanyList;
    private Company company;
}
