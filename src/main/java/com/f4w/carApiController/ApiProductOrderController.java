package com.f4w.carApiController;

import com.f4w.annotation.CurrentUser;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.Product;
import com.f4w.entity.ProductOrder;
import com.f4w.entity.SysUser;
import com.f4w.mapper.ProductMapper;
import com.f4w.mapper.ProductOrderMapper;
import com.f4w.utils.ShowException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.UUID;

/**
 * @Author: yp
 * @Date: 2020/9/8 10:57
 */
@RestController
@RequestMapping("/api/productOrder")
public class ApiProductOrderController {
    @Resource
    private ProductOrderMapper mapper;
    @Resource
    private ProductMapper productMapper;

    /**
     * 兑换商品
     *
     * @param req
     * @return
     */
    @GetMapping("/submit")
    public void submit(@CurrentUser SysUser sysUser, CommonPageReq req) throws ShowException {
        if (req.getId() == null) {
            throw new ShowException("订单id不能为空");
        }
        Product product = Optional.ofNullable(productMapper.selectByPrimaryKey(req.getId())).orElseThrow(() -> new ShowException("商品id错误"));
        if (product.getStatus() != 1) {
            throw new ShowException("商品已经下架");
        }
        if (0 == product.getType() && sysUser.getIntegral() < product.getPrice().intValue()) {
            throw new ShowException("积分不足");
        }
        if (1 == product.getType() && product.getPrice().compareTo(sysUser.getAmount()) < 0) {
            throw new ShowException("余额不足");
        }
        mapper.insertSelective(ProductOrder.builder()
                .productId(product.getId())
                .orderNo(UUID.randomUUID().toString().replaceAll("-", ""))
                .orderType(product.getType())
                .status(0)
                .userId(sysUser.getId())
                .originalPrice(product.getPrice())
                .amount(product.getPrice())
                .des(product.getName())
                .build());
    }

}
