package com.f4w.mapper;

import com.f4w.entity.BusiArticle;
import com.f4w.dto.BusiArticleDto;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.Map;

public interface BusiArticleMapper extends BaseMapper<BusiArticle> {

    @Select("select * from busi_article")
    @Results(
            id = "base",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "uid", column = "uid"),
                    @Result(property = "delete", column = "delete"),
                    @Result(property = "recommend", column = "recommend"),
                    @Result(property = "img", column = "img"),
                    @Result(property = "title", column = "title"),
                    @Result(property = "ctime", column = "ctime"),
                    @Result(property = "mtime", column = "mtime"),
            }
    )
    BusiArticle findAll();
    @Select("select * from busi_article")
    @Results(
            id = "baseDto",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "uid", column = "uid"),
                    @Result(property = "delete", column = "delete"),
                    @Result(property = "recommend", column = "recommend"),
                    @Result(property = "img", column = "img"),
                    @Result(property = "title", column = "title"),
                    @Result(property = "ctime", column = "ctime"),
                    @Result(property = "mtime", column = "mtime"),
            }
    )
    BusiArticleDto findDtoAll();

    void getTag(Map map);
}
