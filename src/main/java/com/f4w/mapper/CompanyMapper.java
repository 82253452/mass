package com.f4w.mapper;

import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.Company;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author admin
 */
public interface CompanyMapper extends BaseMapper<Company> {

    @Select("select * from company where `delete` = 0 order by mtime desc ")
    List<Company> getList(CommonPageReq req);
}
