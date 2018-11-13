package com.f4w.mapper;

import com.f4w.entity.BusiAppPage;
import com.f4w.dto.BusiAppPageDto;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.*;

public interface BusiAppPageMapper extends BaseMapper<BusiAppPage> {

    @Select("select * from busi_app_page")
    @Results(
            id = "base",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "appId", column = "app_id"),
                    @Result(property = "content", column = "content"),
                    @Result(property = "pageName", column = "page_name"),
                    @Result(property = "appSecret", column = "app_secret"),
                    @Result(property = "ctime", column = "ctime"),
                    @Result(property = "mtime", column = "mtime"),
                    @Result(property = "delete", column = "delete"),
            }
    )
    BusiAppPage findAll();
    @Select("select * from busi_app_page")
    @Results(
            id = "baseDto",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "appId", column = "app_id"),
                    @Result(property = "content", column = "content"),
                    @Result(property = "pageName", column = "page_name"),
                    @Result(property = "appSecret", column = "app_secret"),
                    @Result(property = "ctime", column = "ctime"),
                    @Result(property = "mtime", column = "mtime"),
                    @Result(property = "delete", column = "delete"),
            }
    )
    BusiAppPageDto findDtoAll();

}
