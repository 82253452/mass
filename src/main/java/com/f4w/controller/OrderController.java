package com.f4w.controller;

import com.f4w.annotation.CurrentUser;
import com.f4w.dto.OrderInfoDto;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.dto.req.OrderReq;
import com.f4w.entity.Order;
import com.f4w.entity.SysUser;
import com.f4w.mapper.OrderMapper;
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
        OrderInfoDto orderInfoDto = orderMapper.selectById(id);
        return Result.ok(orderInfoDto);
    }

    @PostMapping("/submit")
    public Result submit(@CurrentUser SysUser sysUser, @RequestBody OrderReq orderReq) throws ForeseenException {
        orderMapper.insertSelective(Order.builder()
                .productId(orderReq.getCarTypeId())
                .addressFrom(orderReq.getAddressFrom())
                .latitudeFrom(orderReq.getLatitudeFrom())
                .latitudeTo(orderReq.getLatitudeTo())
                .longitudeFrom(orderReq.getLongitudeFrom())
                .longitudeTo(orderReq.getLongitudeTo())
                .amount(orderReq.getAmount())
                .userId(sysUser.getId().intValue())
                .originalPrice(orderReq.getAmount())
                .userName(orderReq.getUserName())
                .status(0)
                .build());
        return Result.ok();
    }

    @GetMapping("/receiveOrder")
    public Result receiveOrder(@CurrentUser SysUser sysUser, String id) throws ForeseenException {
        Order order = Optional.ofNullable(orderMapper.selectByPrimaryKey(id)).orElseThrow(() -> new ShowException("订单id错误"));
        if (order.getStatus().equals(0)) {
            order.setStatus(1);
            order.setReceiveUserId(sysUser.getId().intValue());
        } else if (order.getStatus().equals(1)) {
            order.setStatus(2);
        } else if (order.getStatus().equals(2)) {
            order.setStatus(3);
        } else {
            return Result.ok();
        }
        orderMapper.updateByPrimaryKeySelective(order);
        return Result.ok();
    }
}
