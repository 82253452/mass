package com.f4w;

import com.f4w.dto.BusiAppDto;
import com.f4w.entity.BusiApp;
import com.f4w.entity.SysUser;
import com.f4w.mapper.BusiAppMapper;
import com.f4w.mapper.SysUserMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.Resource;

/**
 * @author admin
 */
@SpringBootApplication
@MapperScan(basePackages = "com.f4w.mapper")
@EnableWebMvc
@RestController
@EnableAspectJAutoProxy
public class FrameApplication {
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private BusiAppMapper busiAppMapper;

    public static void main(String[] args) {
        SpringApplication.run(FrameApplication.class, args);
    }

    @RequestMapping("/")
    public String test() {
        return "123123";
    }
}
