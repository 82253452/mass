package com.f4w.controller;

import com.f4w.dto.req.MiniAppLoginReq;
import com.f4w.entity.SysUser;
import com.f4w.service.WechatService;
import com.f4w.utils.ForeseenException;
import com.f4w.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author: yp
 * @Date: 2020/9/8 10:57
 */
@RestController
@RequestMapping("/mini")
public class MiniAppController {
    @Resource
    private WechatService wechatService;

    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody @Valid MiniAppLoginReq request) throws ForeseenException {
        SysUser sysUser = wechatService.loginFromOpenThird(request);
        return Result.ok(sysUser);
    }

    @GetMapping("/user")
    public Result<SysUser> user(MiniAppLoginReq request) throws ForeseenException {
        SysUser sysUser = wechatService.getUserInfoByCode(request);
        return Result.ok(sysUser);
    }

    @PostMapping("/getPhoneNoInfo")
    private Result<String> getPhoneNoInfo(@RequestBody @Valid MiniAppLoginReq request) throws ForeseenException {
        String phone = wechatService.getPhoneNoInfo(request);
        return Result.ok(phone);
    }
}
