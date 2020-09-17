package com.f4w.controller;

import com.f4w.dto.req.CarTypeReq;
import com.f4w.entity.CarType;
import com.f4w.entity.TransCompany;
import com.f4w.mapper.CarTypeMapper;
import com.f4w.service.WechatService;
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
@RequestMapping("/car")
public class CarTypeController {
    @Resource
    private CarTypeMapper carTypeMapper;

    @GetMapping("/list")
    public Result<PageInfo<CarType>> list(CarTypeReq req) throws ForeseenException {
        PageInfo<CarType> page = PageInfo.of(carTypeMapper.getList(req));
        return Result.ok(page);
    }

    @PostMapping
    public Result add(@RequestBody CarType req) throws ForeseenException {
        int i = carTypeMapper.insertSelective(req);
        return Result.ok(i);
    }

    @PutMapping
    public Result update(@RequestBody CarType req) throws ForeseenException {
        int i = carTypeMapper.updateByPrimaryKeySelective(req);
        return Result.ok(i);
    }

    @DeleteMapping("{id}")
    public Result delete(@PathVariable String id) throws ForeseenException {
        int i = carTypeMapper.deleteByPrimaryKey(id);
        return Result.ok(i);
    }

}
