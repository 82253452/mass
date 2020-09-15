package com.f4w.mapper;

import com.f4w.dto.OrderInfoDto;
import com.f4w.dto.req.CarTypeReq;
import com.f4w.dto.req.CommonPageReq;
import com.f4w.entity.CarType;
import com.f4w.entity.Order;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author admin
 */
public interface OrderMapper extends BaseMapper<Order> {


    /**
     * car type list
     *
     * @param req
     * @return
     */
    @Select("select o.*, \n" +
            "       " +
            "title,\n" +
            "       type,\n" +
            "       car_length,\n" +
            "       car_width,\n" +
            "       car_height,\n" +
            "       carrying_capacity,\n" +
            "       img\n" +
            "from `order` o\n" +
            "         left join car_type ct on o.product_id = ct.id\n" +
            "where o.delete = 0\n" +
            "and o.status = #{status}\n" +
            "order by o.mtime desc")
    List<OrderInfoDto> getList(CommonPageReq req);

    @Select("select o.*, \n" +
            "       " +
            "title,\n" +
            "       type,\n" +
            "       car_length,\n" +
            "       car_width,\n" +
            "       car_height,\n" +
            "       carrying_capacity,\n" +
            "       img\n" +
            "from `order` o\n" +
            "         left join car_type ct on o.product_id = ct.id\n" +
            "where o.delete = 0\n" +
            "and o.id = #{id}\n" +
            "order by o.mtime desc")
    OrderInfoDto selectById(String id);
}
