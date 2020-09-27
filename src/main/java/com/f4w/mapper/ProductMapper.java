package com.f4w.mapper;

import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.Product;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {

    @Select("select * from product where `delete` = 0 order by mtime desc ")
    List<Product> getList(CommonPageReq req);
}
