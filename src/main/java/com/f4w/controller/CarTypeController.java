package com.f4w.controller;

import com.f4w.dto.req.CarTypeReq;
import com.f4w.entity.CarType;
import com.f4w.mapper.CarTypeMapper;
import com.f4w.service.WechatService;
import com.f4w.utils.ForeseenException;
import com.f4w.utils.Result;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
