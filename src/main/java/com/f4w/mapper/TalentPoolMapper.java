package com.f4w.mapper;

import com.f4w.dto.req.TalentPoolReq;
import com.f4w.entity.TalentPool;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TalentPoolMapper extends BaseMapper<TalentPool> {

    @Select({"<script>",
            "select * from talent_pool where 1=1",
            "<when test='edu!=null'>",
            "AND education = #{edu}",
            "</when>",
            "<when test='phone!=null'>",
            "AND phone = #{phone}",
            "</when>",
            "<when test='sc!=null'>",
            "AND sc = #{sc}",
            "</when>",
            "and del =0",
            "</script>"
    })
    List<TalentPool> selectAllByPage(TalentPoolReq req);
}
