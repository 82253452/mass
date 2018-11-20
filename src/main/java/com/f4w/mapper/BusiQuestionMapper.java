package com.f4w.mapper;

import com.f4w.entity.BusiQuestion;
import com.f4w.dto.BusiQuestionDto;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.*;

public interface BusiQuestionMapper extends BaseMapper<BusiQuestion> {

    @Select("select * from busi_question")
    @Results(
            id = "base",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "title", column = "title"),
                    @Result(property = "questions", column = "questions"),
                    @Result(property = "ctime", column = "ctime"),
                    @Result(property = "mtime", column = "mtime"),
                    @Result(property = "delete", column = "delete"),
                    @Result(property = "status", column = "status"),
                    @Result(property = "right", column = "right"),
                    @Result(property = "appId", column = "app_id"),
                    @Result(property = "uid", column = "uid"),
            }
    )
    BusiQuestion findAll();

    @Select("select * from busi_question")
    @Results(
            id = "baseDto",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "title", column = "title"),
                    @Result(property = "questions", column = "questions"),
                    @Result(property = "ctime", column = "ctime"),
                    @Result(property = "mtime", column = "mtime"),
                    @Result(property = "delete", column = "delete"),
                    @Result(property = "status", column = "status"),
                    @Result(property = "right", column = "right"),
                    @Result(property = "appId", column = "app_id"),
                    @Result(property = "uid", column = "uid"),
            }
    )
    BusiQuestionDto findDtoAll();

    @Select("select  * from busi_question where title like '%' #{title} '%' and app_id=#{appId} limit 1")
    @ResultMap("baseDto")
    BusiQuestionDto getOneListQuestion(@Param("title") String title, @Param("appId") String appId);

}
