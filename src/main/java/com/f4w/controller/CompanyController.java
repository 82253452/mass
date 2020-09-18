package com.f4w.controller;

import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.Company;
import com.f4w.mapper.CompanyMapper;
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
@RequestMapping("/company")
public class CompanyController {
    @Resource
    private CompanyMapper mapper;

    @GetMapping("/list")
    public Result<PageInfo<Company>> list(CommonPageReq req) throws ForeseenException {
        PageInfo<Company> page = PageInfo.of(mapper.getList(req));
        return Result.ok(page);
    }

    @GetMapping("/detail")
    public Result<Company> detail(String id) throws ForeseenException {
        Company transCompany = mapper.selectByPrimaryKey(id);
        return Result.ok(transCompany);
    }

    @PostMapping
    public Result add(@RequestBody Company banner) throws ForeseenException {
        int i = mapper.insertSelective(banner);
        return Result.ok(i);
    }

    @PutMapping
    public Result update(@RequestBody Company banner) throws ForeseenException {
        int i = mapper.updateByPrimaryKeySelective(banner);
        return Result.ok(i);
    }

    @DeleteMapping("{id}")
    public Result delete(@PathVariable String id) throws ForeseenException {
        int i = mapper.deleteByPrimaryKey(id);
        return Result.ok(i);
    }

}
