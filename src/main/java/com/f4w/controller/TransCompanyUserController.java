package com.f4w.controller;

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
import com.f4w.utils.Result;
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
    public Result<PageInfo<TransCompanyUserDto>> list(CommonPageReq req) throws ForeseenException {
        PageInfo<TransCompanyUserDto> page = PageInfo.of(mapper.getList(req));
        return Result.ok(page);
    }

    @GetMapping("/admin/list")
    public Result<PageInfo<TransCompanyUserDto>> adminList(@CurrentUser SysUser sysUser, TransPageReq req) throws ForeseenException {

        List<SysRoleDto> roleDtos = sysRoleMapper.getRolesByUserId(sysUser.getId());
        if (roleDtos.contains("admin")) {

        } else if (roleDtos.contains("trans")) {
            if (sysUser.getTransId() == null) {
                throw new ShowException("无权限");
            }
            req.setTransId(sysUser.getTransId());
        } else {
            req.setUserId(sysUser.getId().intValue());
        }
        PageInfo<TransCompanyUserDto> page = PageInfo.of(mapper.getAdminList(req));
        return Result.ok(page);
    }

    @GetMapping("/checkUser")
    public Result checkUser(Integer id, Integer status) throws ForeseenException {
        TransCompanyUser transCompanyUser = mapper.selectByPrimaryKey(id);
        if (transCompanyUser.getStatus().equals(0)) {
            transCompanyUser.setStatus(status);
            mapper.updateByPrimaryKeySelective(transCompanyUser);
        }
        return Result.ok(1);
    }

    @DeleteMapping
    public Result delete(@PathVariable Integer id) throws ForeseenException {
        mapper.deleteByPrimaryKey(id);
        return Result.ok(1);
    }

}
