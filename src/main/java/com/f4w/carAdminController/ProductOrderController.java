package com.f4w.carAdminController;

import com.f4w.dto.ProductOrderDto;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.Product;
import com.f4w.entity.ProductOrder;
import com.f4w.mapper.ProductMapper;
import com.f4w.mapper.ProductOrderMapper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: yp
 * @Date: 2020/9/8 10:57
 */
@RestController
@RequestMapping("/productOrder")
public class ProductOrderController {
    @Resource
    private ProductOrderMapper mapper;

    @GetMapping("/list")
    public PageInfo<ProductOrderDto> list(CommonPageReq req) {
        PageInfo<ProductOrderDto> page = PageInfo.of(mapper.getList(req));
        return page;
    }

    @GetMapping("/detail")
    public ProductOrder detail(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    @PostMapping
    public int add(@RequestBody ProductOrder entity) {
        return mapper.insertSelective(entity);
    }

    @PutMapping
    public int update(@RequestBody ProductOrder entity) {
        return mapper.updateByPrimaryKeySelective(entity);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        mapper.deleteByPrimaryKey(id);
    }


}
