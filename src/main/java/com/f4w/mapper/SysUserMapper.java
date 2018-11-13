package com.f4w.mapper;

import com.f4w.utils.BaseMapper;
import com.f4w.dto.BusiAppDto;
import com.f4w.entity.BusiApp;
import com.f4w.entity.SysUser;
import org.apache.ibatis.annotations.*;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("select * from busi_app")
    @Results(
            id = "base",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "appId", column = "app_id")
            }
    )
    BusiApp findAll();

    @Select("select * from busi_app")
    @Results(
            id = "baseDto",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "appId", column = "app_id")
            }
    )
    BusiAppDto findDtoAll();
}