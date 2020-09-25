package com.f4w.carApiController;

import com.f4w.entity.Driver;
import com.f4w.mapper.DriverMapper;
import com.f4w.utils.ForeseenException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: yp
 * @Date: 2020/9/8 10:57
 */
@RestController
@RequestMapping("/api/driver")
public class ApiDriverController {
    @Resource
    private DriverMapper driverMapper;

    @PostMapping("/register")
    public int register(@RequestBody Driver driver)  {
        int i = driverMapper.insertSelective(driver);
        return i;
    }

}
