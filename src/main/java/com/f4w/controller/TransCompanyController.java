package com.f4w.controller;

import com.f4w.annotation.CurrentUser;
import com.f4w.dto.TransCompanyDto;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.SysUser;
import com.f4w.entity.TransCompany;
import com.f4w.entity.TransCompanyUser;
import com.f4w.mapper.TransCompanyMapper;
import com.f4w.mapper.TransCompanyUserMapper;
import com.f4w.utils.ForeseenException;
import com.f4w.utils.Result;
import com.f4w.utils.ShowException;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @Author: yp
 * @Date: 2020/9/8 10:57
 */
@RestController
@RequestMapping("/trans")
public class TransCompanyController {
    @Resource
    private TransCompanyMapper mapper;
    @Resource
    private TransCompanyUserMapper transCompanyUserMapper;

    @GetMapping("/list")
    public Result<PageInfo<TransCompany>> list(CommonPageReq req) throws ForeseenException {
        PageInfo<TransCompany> page = PageInfo.of(mapper.getList(req));
        return Result.ok(page);
    }

    @GetMapping("/user/list")
    public Result<PageInfo<TransCompanyDto>> userList(@CurrentUser SysUser sysUser, CommonPageReq req) throws ForeseenException {
        req.setUserId(sysUser.getId().intValue());
        PageInfo<TransCompanyDto> page = PageInfo.of(mapper.getUserList(req));
        return Result.ok(page);
    }

    @GetMapping("/detail")
    public Result<TransCompany> detail(String id) throws ForeseenException {
        TransCompany transCompany = mapper.selectByPrimaryKey(id);
        return Result.ok(transCompany);
    }

    @PostMapping
    public Result add(@RequestBody TransCompany banner) throws ForeseenException {
        int i = mapper.insertSelective(banner);
        return Result.ok(i);
    }

    @PutMapping
    public Result update(@RequestBody TransCompany banner) throws ForeseenException {
        int i = mapper.updateByPrimaryKeySelective(banner);
        return Result.ok(i);
    }

    @DeleteMapping("{id}")
    public Result delete(@PathVariable String id) throws ForeseenException {
        int i = mapper.deleteByPrimaryKey(id);
        return Result.ok(i);
    }

    @GetMapping("/attachCompany")
    public Result attachCompany(@CurrentUser SysUser sysUser, Integer transId) throws ForeseenException {
        TransCompany transCompany = Optional.ofNullable(mapper.selectByPrimaryKey(transId)).orElseThrow(() -> new ShowException("参数错误"));
        TransCompanyUser transCompanyUser = transCompanyUserMapper.selectOne(TransCompanyUser.builder().userId(sysUser.getId().intValue()).transId(transCompany.getId().intValue()).build());
        if (transCompanyUser == null) {
            transCompanyUserMapper.insertSelective(TransCompanyUser.builder().transId(transId).userId(sysUser.getId().intValue()).build());
            return Result.ok();
        }
        if (transCompanyUser.getStatus() == 0) {
            throw new ShowException("已经申请加入该公司");
        }
        if (transCompanyUser.getStatus() == 1) {
            throw new ShowException("已经加入该公司");
        }
        if (transCompanyUser.getStatus() == 2) {
            transCompanyUser.setStatus(0);
            transCompanyUserMapper.updateByPrimaryKeySelective(transCompanyUser);
        }
        return Result.ok();
    }

}
