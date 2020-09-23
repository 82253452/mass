package com.f4w.carAdminController;

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
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;

    @GetMapping("/list")
    public PageInfo<OrderInfoDto> list(CommonPageReq req) throws ForeseenException {
        PageInfo<OrderInfoDto> page = PageInfo.of(orderMapper.getList(req));
        return page;
    }

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

    @GetMapping("/admin/list")
    public PageInfo<OrderInfoDto> adminList(@CurrentUser SysUser sysUser, OrderPageReq req) throws ForeseenException {
        List<SysRoleDto> roleDtos = sysRoleMapper.getRolesByUserId(sysUser.getId());
        if (roleDtos.stream().anyMatch(r -> r.getRoleName().equals("admin"))) {

        } else if (roleDtos.stream().anyMatch(r -> r.getRoleName().equals("trans"))) {
            if (sysUser.getTransId() == null) {
                throw new ShowException("无权限");
            }
            req.setTransId(sysUser.getTransId());
        } else {
            req.setUserId(sysUser.getId().intValue());
        }
        PageInfo<OrderInfoDto> page = PageInfo.of(orderMapper.getAdminList(req));
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
                .phone(orderReq.getPhone())
                .orderNo(UUID.randomUUID().toString().replaceAll("-", ""))
                .productId(orderReq.getCarTypeId())
                .addressFrom(orderReq.getAddressFrom())
                .addressTo(orderReq.getAddressTo())
                .des(orderReq.getDes())
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
