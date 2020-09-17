package com.f4w.mapper;

import com.f4w.dto.TransCompanyUserDto;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.dto.req.TransPageReq;
import com.f4w.entity.TransCompanyUser;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author admin
 */
public interface TransCompanyUserMapper extends BaseMapper<TransCompanyUser> {

    @Select("select tcu.id id,su.*, tcu.status\n" +
            "from trans_company_user tcu\n" +
            "         left join sys_user su on tcu.user_id = su.id")
    List<TransCompanyUserDto> getList(CommonPageReq req);

    @Select("select tcu.id id,su.*, tcu.status\n" +
            "from trans_company_user tcu\n" +
            "         left join sys_user su on tcu.user_id = su.id")
    List<TransCompanyUserDto> getAdminList(TransPageReq req);
}
