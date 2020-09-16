package com.f4w.controller;

import com.f4w.dto.req.MiniAppLoginReq;
import com.f4w.entity.SysUser;
import com.f4w.service.WechatService;
import com.f4w.utils.ForeseenException;
import com.f4w.utils.R;
import com.f4w.utils.Result;
import com.f4w.weapp.WxOpenService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.WxOpenMaCodeTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yp
 * @Date: 2020/9/8 10:57
 */
@RestController
@RequestMapping("/mini")
public class MiniAppController {
    @Resource
    private WechatService wechatService;
    @Resource
    private WxOpenService wxOpenService;

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

    /**
     * 获取草稿箱内的所有临时代码草稿
     *
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/getTrafts")
    public R getTrafts() throws WxErrorException {
        List<WxOpenMaCodeTemplate> templateDraftList = wxOpenService.getWxOpenComponentService().getTemplateDraftList();
        return R.ok(templateDraftList);
    }

    /**
     * 获取代码模版库中的所有小程序代码模版
     *
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/getTemplates")
    public R getTemplates() throws WxErrorException {
        List<WxOpenMaCodeTemplate> templateList = wxOpenService.getWxOpenComponentService().getTemplateList();
        return R.ok(templateList);
    }
}
