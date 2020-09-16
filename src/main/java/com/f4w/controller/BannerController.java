package com.f4w.controller;

import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.Banner;
import com.f4w.mapper.BannerMapper;
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
@RequestMapping("/banner")
public class BannerController {
    @Resource
    private BannerMapper bannerMapper;

    @GetMapping("/list")
    public Result<PageInfo<Banner>> list(CommonPageReq req) throws ForeseenException {
        PageInfo<Banner> page = PageInfo.of(bannerMapper.getList(req));
        return Result.ok(page);
    }

    @PostMapping
    public Result add(@RequestBody Banner banner) throws ForeseenException {
        bannerMapper.insertSelective(banner);
        return Result.ok();
    }

    @PutMapping
    public Result update(@RequestBody Banner banner) throws ForeseenException {
        bannerMapper.updateByPrimaryKeySelective(banner);
        return Result.ok();
    }

    @DeleteMapping
    public Result delete(String id) throws ForeseenException {
        bannerMapper.deleteByPrimaryKey(id);
        return Result.ok();
    }

}
