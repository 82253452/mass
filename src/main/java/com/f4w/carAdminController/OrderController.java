package com.f4w.carAdminController;

import com.f4w.annotation.CurrentUser;
import com.f4w.dto.OrderInfoDto;
import com.f4w.dto.SysRoleDto;
import com.f4w.dto.enums.OrderStatusEnum;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.dto.req.OrderPageReq;
import com.f4w.dto.req.OrderReq;
import com.f4w.entity.Order;
import com.f4w.entity.SysUser;
import com.f4w.entity.TransCompany;
import com.f4w.mapper.OrderMapper;
import com.f4w.mapper.SysRoleMapper;
import com.f4w.mapper.TransCompanyMapper;
import com.f4w.utils.ForeseenException;
import com.f4w.utils.ShowException;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
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
    @Resource
    private TransCompanyMapper transCompanyMapper;

    @GetMapping("/list")
    public PageInfo<OrderInfoDto> list(CommonPageReq req) {
        PageInfo<OrderInfoDto> page = PageInfo.of(orderMapper.getList(req));
        return page;
    }

    /**
     * 抢单
     *
     * @param req
     * @return
     * @
     */
    @GetMapping("/index/list")
    public PageInfo<OrderInfoDto> indexList(CommonPageReq req) {
        PageInfo<OrderInfoDto> page = PageInfo.of(orderMapper.getIndexList(req));
        return page;
    }

    /**
     * 查询我的订单
     *
     * @param req
     * @return
     * @
     */
    @GetMapping("/status/list")
    public PageInfo<OrderInfoDto> statusList(@CurrentUser SysUser sysUser, CommonPageReq req) {
        req.setUserId(sysUser.getId().intValue());
        PageInfo<OrderInfoDto> page = PageInfo.of(orderMapper.getStatusList(req));
        return page;
    }

    /**
     * 完成得单子
     *
     * @param req
     * @return
     * @
     */
    @GetMapping("/finash/list")
    public PageInfo<OrderInfoDto> fianshList(@CurrentUser SysUser sysUser, CommonPageReq req) {
        req.setUserId(sysUser.getId().intValue());
        PageInfo<OrderInfoDto> page = PageInfo.of(orderMapper.getFinashList(req));
        return page;
    }

    @GetMapping("/admin/list")
    public PageInfo<OrderInfoDto> adminList(@CurrentUser SysUser sysUser, OrderPageReq req) throws ShowException {
        List<SysRoleDto> roleDtos = sysRoleMapper.getRolesByUserId(sysUser.getId());
        if (roleDtos.stream().anyMatch(r -> r.getRoleName().equals("admin"))) {

        } else if (roleDtos.stream().anyMatch(r -> r.getRoleName().equals("trans"))) {
            TransCompany transCompany = transCompanyMapper.selectOne(TransCompany.builder().userId(sysUser.getId()).build());
            if (transCompany == null) {
                throw new ShowException("无权限");
            }
            req.setTransId(transCompany.getId());
        } else {
            req.setUserId(sysUser.getId());
        }
        PageInfo<OrderInfoDto> page = PageInfo.of(orderMapper.getAdminList(req));
        return page;
    }

    @GetMapping("/detail")
    public OrderInfoDto selectById(String id) {
        OrderInfoDto orderInfoDto = orderMapper.selectById(id);
        return orderInfoDto;
    }

    @PutMapping
    public int update(@RequestBody Order order) {
        return orderMapper.updateByPrimaryKeySelective(order);
    }

    @DeleteMapping("/{id}")
    public int delete(@PathVariable String id) {
        return orderMapper.deleteByPrimaryKey(id);
    }

    @GetMapping("/orderToDriver")
    public void orderToDriver(@NotNull Integer orderId, @NotNull Integer userId) throws ShowException {
        Order order = Optional.ofNullable(orderMapper.selectByPrimaryKey(orderId)).orElseThrow(() -> new ShowException("订单id错误"));
        if (order.getStatus() != 0) {
            throw new ShowException("订单状态错误");
        }
        order.setStatus(OrderStatusEnum.TRANS_COMPANY1.getCode());
        order.setReceiveUserId(userId);
        orderMapper.updateByPrimaryKeySelective(order);
    }
}
