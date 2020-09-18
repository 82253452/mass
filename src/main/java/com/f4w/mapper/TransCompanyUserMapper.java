package com.f4w.mapper;

import com.f4w.dto.TransCompanyUserDto;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.dto.req.TransPageReq;
import com.f4w.entity.TransCompany;
import com.f4w.entity.TransCompanyUser;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.Param;
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

    @Select("<script>\n" +
            "select tcu.id id,su.*, tcu.status\n" +
            "from trans_company_user tcu\n" +
            "         left join sys_user su on tcu.user_id = su.id\n" +
            "    where 1=1\n" +
            "    <if test='transId!=null and transId != &quot;&quot; '>\n" +
            "        and tcu.trans_id = #{transId}\n" +
            "    </if>\n" +
            "</script>")
    List<TransCompanyUserDto> getAdminList(TransPageReq req);

    @Select("select tc.*\n" +
            "from trans_company_user tcu\n" +
            "         left join trans_company tc on tcu.trans_id = tc.id\n" +
            "where tcu.user_id = #{userId}")
    List<TransCompany> getUserCompanyList(@Param("userId") Integer userId);
}
