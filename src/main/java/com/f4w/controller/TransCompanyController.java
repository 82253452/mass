package com.f4w.controller;

import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.TransCompany;
import com.f4w.mapper.TransCompanyMapper;
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
@RequestMapping("/trans")
public class TransCompanyController {
    @Resource
    private TransCompanyMapper mapper;

    @GetMapping("/list")
    public Result<PageInfo<TransCompany>> list(CommonPageReq req) throws ForeseenException {
        PageInfo<TransCompany> page = PageInfo.of(mapper.getList(req));
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

}
