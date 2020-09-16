package com.f4w.mapper;

import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.TransCompany;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author admin
 */
public interface TransCompanyMapper extends BaseMapper<TransCompany> {

    @Select("select * from trans_company where `delete` = 0 order by mtime desc ")
    List<TransCompany> getList(CommonPageReq req);
}
