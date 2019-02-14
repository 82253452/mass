package com.f4w.mapper;

import com.f4w.dto.WxmpDto;
import com.f4w.entity.Wxmp;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface WxmpMapper extends BaseMapper<Wxmp> {

    @Select("select * from wxmp")
    @Results(
            id = "base",
            value = {
                    @Result(property = "type", column = "type"),
                    @Result(property = "title", column = "title"),
                    @Result(property = "thumbnail", column = "thumbnail"),
                    @Result(property = "summary", column = "summary"),
                    @Result(property = "videoId", column = "video_id"),
                    @Result(property = "auther", column = "auther"),
                    @Result(property = "columnId", column = "column_id"),
                    @Result(property = "ctime", column = "ctime"),
                    @Result(property = "status", column = "status"),
                    @Result(property = "mtime", column = "mtime"),
                    @Result(property = "delete", column = "delete"),
            }
    )
    Wxmp findAll();

    @Select("select * from wxmp")
    @Results(
            id = "baseDto",
            value = {
                    @Result(property = "type", column = "type"),
                    @Result(property = "title", column = "title"),
                    @Result(property = "thumbnail", column = "thumbnail"),
                    @Result(property = "summary", column = "summary"),
                    @Result(property = "videoId", column = "video_id"),
                    @Result(property = "auther", column = "auther"),
                    @Result(property = "columnId", column = "column_id"),
                    @Result(property = "ctime", column = "ctime"),
                    @Result(property = "status", column = "status"),
                    @Result(property = "mtime", column = "mtime"),
                    @Result(property = "delete", column = "delete"),
            }
    )
    WxmpDto findDtoAll();

    @Select("select column_id from wxmp group by column_id order by  column_id")
    List<String> findColumns();

    @Delete("update wxmp wx set del ='1'  WHERE wx.id IN (select t.id from (SELECT max(id) as id FROM wxmp where del =0  GROUP BY title HAVING count(*) > 1) t)")
    void deleteDuplicates();

    @Delete("update wxmp wx set del ='1'  WHERE wx.id = #{id}")
    void deleteById(Long id);
}
