package com.f4w.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ColumnWidth(30)
public class TalentPool extends BaseEntity {
    @ExcelProperty("id")
    private String pid;
    private String tid;
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("手机号")
    private String phone;
    @ExcelProperty("毕业院校")
    private String sc;
    @ExcelProperty("基本信息")
    private String base;
    private String education;
    private Date updateTime;
    private String major;
    private String remark;
    private Integer status;

}
