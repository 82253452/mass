package com.f4w.carApiController;

import com.f4w.annotation.CurrentUser;
import com.f4w.dto.OrderInfoDto;
import com.f4w.dto.SysRoleDto;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.dto.req.OrderPageReq;
import com.f4w.dto.req.OrderReq;
import com.f4w.entity.Order;
import com.f4w.entity.SysUser;
import com.f4w.mapper.OrderMapper;
import com.f4w.mapper.SysRoleMapper;
import com.f4w.utils.ForeseenException;
import com.f4w.utils.ShowException;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @Author: yp
 * @Date: 2020/9/8 10:57
 */
@RestController
@RequestMapping("/api/order")
public class ApiOrderController {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;

    /**
     * 抢单
     *
     * @param req
     * @return
     * @throws ForeseenException
     */
    @GetMapping("/index/list")
    public PageInfo<OrderInfoDto> indexList(CommonPageReq req) throws ForeseenException {
        PageInfo<OrderInfoDto> page = PageInfo.of(orderMapper.getIndexList(req));
        return page;
    }

    /**
     * 查询我的订单
     *
     * @param req
     * @return
     * @throws ForeseenException
     */
    @GetMapping("/status/list")
    public PageInfo<OrderInfoDto> statusList(@CurrentUser SysUser sysUser, CommonPageReq req) throws ForeseenException {
        req.setUserId(sysUser.getId().intValue());
        PageInfo<OrderInfoDto> page = PageInfo.of(orderMapper.getStatusList(req));
        return page;
    }

    /**
     * 完成得单子
     *
     * @param req
     * @return
     * @throws ForeseenException
     */
    @GetMapping("/finash/list")
    public PageInfo<OrderInfoDto> fianshList(@CurrentUser SysUser sysUser, CommonPageReq req) throws ForeseenException {
        req.setUserId(sysUser.getId().intValue());
        PageInfo<OrderInfoDto> page = PageInfo.of(orderMapper.getFinashList(req));
        return page;
    }

    @GetMapping("/detail")
    public OrderInfoDto selectById(String id) throws ForeseenException {
        OrderInfoDto orderInfoDto = orderMapper.selectById(id);
        return orderInfoDto;
    }

    @PutMapping
    public int update(@RequestBody Order order) throws ForeseenException {
        return orderMapper.updateByPrimaryKeySelective(order);
    }

    @DeleteMapping("/{id}")
    public int delete(@PathVariable String id) throws ForeseenException {
        return orderMapper.deleteByPrimaryKey(id);
    }

    @PostMapping("/submit")
    public void submit(@CurrentUser SysUser sysUser, @RequestBody OrderReq orderReq) throws ForeseenException {
        orderMapper.insertSelective(Order.builder()
                .phone(orderReq.getAddressFrom().getUser().getPhone())
                .orderNo(UUID.randomUUID().toString().replaceAll("-", ""))
                .productId(orderReq.getCarTypeId())
                .addressFrom(orderReq.getAddressFrom().getLocation().getName())
                .addressTo(orderReq.getAddressTo().getLocation().getName())
                .des(orderReq.getDes())
                .latitudeFrom(orderReq.getAddressFrom().getLocation().getLatitude())
                .latitudeTo(orderReq.getAddressTo().getLocation().getLatitude())
                .longitudeFrom(orderReq.getAddressFrom().getLocation().getLongitude())
                .longitudeTo(orderReq.getAddressTo().getLocation().getLongitude())
                .amount(orderReq.getAmount())
                .userId(sysUser.getId())
                .originalPrice(orderReq.getAmount())
                .userName(orderReq.getAddressFrom().getUser().getName())
                .discharge(orderReq.getDischarge())
                .deliveryTimeStart(DateTime.parse(orderReq.getTime()[0]).toDate())
                .deliveryTimeEnd(DateTime.parse(orderReq.getTime()[1]).toDate())
                .driverTop(orderReq.getDriverTop())
                .addressToDetail(orderReq.getAddressTo().getLocation().getAddress())
                .addressFromDetail(orderReq.getAddressFrom().getLocation().getAddress())
                .remark(orderReq.getRemark())
                .addressRoute(StringUtils.join(orderReq.getAddressRoute(),","))
                .addressFromHome(orderReq.getAddressFrom().getUser().getAddress())
                .addressToHome(orderReq.getAddressTo().getUser().getAddress())
                .receiveName(orderReq.getAddressTo().getUser().getName())
                .receivePhone(orderReq.getAddressTo().getUser().getPhone())
                .status(0)
                .build());
    }

    @GetMapping("/receiveOrder")
    public void receiveOrder(@CurrentUser SysUser sysUser, String id) throws ForeseenException {
        Order order = Optional.ofNullable(orderMapper.selectByPrimaryKey(id)).orElseThrow(() -> new ShowException("订单id错误"));
        if (order.getStatus() == 0) {
            order.setReceiveUserId(sysUser.getId().intValue());
        }
        if (order.getStatus() < 4) {
            order.setStatus(order.getStatus() + 1);
            orderMapper.updateByPrimaryKeySelective(order);
        }
    }
}
