package com.f4w.mapper;

import com.f4w.dto.OrderInfoDto;
import com.f4w.dto.ProductOrderDto;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.Order;
import com.f4w.entity.ProductOrder;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author admin
 */
public interface ProductOrderMapper extends BaseMapper<ProductOrder> {

    @Select("<script>\n" +
            "select o.*, su.user_name userName, p.name productName\n" +
            "from `product_order` o\n" +
            "         left join sys_user su on o.user_id = su.id\n" +
            "         left join product p on o.product_id = p.id\n" +
            "where o.delete = 0\n" +
            "order by o.mtime desc\n" +
            "</script>")
    List<ProductOrderDto> getList(CommonPageReq req);
}
