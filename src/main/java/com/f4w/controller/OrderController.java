package com.f4w.controller;

import com.f4w.dto.OrderInfoDto;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.mapper.OrderMapper;
import com.f4w.utils.ForeseenException;
import com.f4w.utils.Result;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: yp
 * @Date: 2020/9/8 10:57
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderMapper orderMapper;

    @GetMapping("/list")
    public Result<PageInfo<OrderInfoDto>> list(CommonPageReq req) throws ForeseenException {
        PageInfo<OrderInfoDto> page = PageInfo.of(orderMapper.getList(req));
        return Result.ok(page);
    }

    @GetMapping("/detail")
    public Result<OrderInfoDto> selectById(String id) throws ForeseenException {
        OrderInfoDto orderInfoDto =  orderMapper.selectById(id);
        return Result.ok(orderInfoDto);
    }
}
