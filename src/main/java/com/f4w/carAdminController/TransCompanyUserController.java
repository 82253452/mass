package com.f4w.carAdminController;

import com.f4w.annotation.CurrentUser;
import com.f4w.dto.SysRoleDto;
import com.f4w.dto.TransCompanyUserDto;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.dto.req.TransPageReq;
import com.f4w.entity.SysUser;
import com.f4w.entity.TransCompanyUser;
import com.f4w.mapper.SysRoleMapper;
import com.f4w.mapper.TransCompanyUserMapper;
import com.f4w.utils.ForeseenException;
import com.f4w.utils.ShowException;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: yp
 * @Date: 2020/9/8 10:57
 */
@RestController
@RequestMapping("/transCompanyUser")
public class TransCompanyUserController {
    @Resource
    private TransCompanyUserMapper mapper;
    @Resource
    private SysRoleMapper sysRoleMapper;

    @GetMapping("/list")
    public PageInfo<TransCompanyUserDto> list(CommonPageReq req) throws ForeseenException {
        PageInfo<TransCompanyUserDto> page = PageInfo.of(mapper.getList(req));
        return page;
    }

    @GetMapping("/admin/list")
    public PageInfo<TransCompanyUserDto> adminList(@CurrentUser SysUser sysUser, TransPageReq req) throws ForeseenException {

        List<SysRoleDto> roleDtos = sysRoleMapper.getRolesByUserId(sysUser.getId());
        if (roleDtos.stream().anyMatch(r -> r.getRoleName().equals("admin"))) {

        } else if (roleDtos.stream().anyMatch(r -> r.getRoleName().equals("trans"))) {
//            if (sysUser.getTransId() == null) {
//                throw new ShowException("无权限");
//            }
//            req.setTransId(sysUser.getTransId());
        } else {
            throw new ShowException("无权限");
        }
        PageInfo<TransCompanyUserDto> page = PageInfo.of(mapper.getAdminList(req));
        return page;
    }

    @GetMapping("/checkUser")
    public void checkUser(Integer id, Integer status) throws ForeseenException {
        TransCompanyUser transCompanyUser = mapper.selectByPrimaryKey(id);
        if (transCompanyUser.getStatus().equals(0) || transCompanyUser.getStatus().equals(2)) {
            transCompanyUser.setStatus(status);
            mapper.updateByPrimaryKeySelective(transCompanyUser);
        }
    }

    @DeleteMapping("{id}")
    public int delete(@PathVariable Integer id) throws ForeseenException {
        return mapper.deleteByPrimaryKey(id);
    }

}
