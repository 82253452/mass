package com.f4w.controller;

import com.f4w.dto.TransCompanyUserDto;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.TransCompanyUser;
import com.f4w.mapper.TransCompanyUserMapper;
import com.f4w.utils.ForeseenException;
import com.f4w.utils.Result;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: yp
 * @Date: 2020/9/8 10:57
 */
@RestController
@RequestMapping("/transCompanyUser")
public class TransCompanyUserController {
    @Resource
    private TransCompanyUserMapper mapper;

    @GetMapping("/list")
    public Result<PageInfo<TransCompanyUserDto>> list(CommonPageReq req) throws ForeseenException {
        PageInfo<TransCompanyUserDto> page = PageInfo.of(mapper.getList(req));
        return Result.ok(page);
    }

    @GetMapping("/checkUser")
    public Result checkUser(Integer id,Integer status) throws ForeseenException {
        TransCompanyUser transCompanyUser = mapper.selectByPrimaryKey(id);
        if (transCompanyUser.getStatus().equals(0)) {
            transCompanyUser.setStatus(status);
            mapper.updateByPrimaryKeySelective(transCompanyUser);
        }
        return Result.ok(1);
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestBody Integer id) throws ForeseenException {
        mapper.deleteByPrimaryKey(id);
        return Result.ok(1);
    }

}
