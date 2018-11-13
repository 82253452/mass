package com.f4w.api;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.f4w.entity.SysUser;
import com.f4w.mapper.SysUserMapper;
import com.f4w.utils.R;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("common")
public class CommonAPI {

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 获取七牛token
     *
     * @return
     */
    @GetMapping("qiniuToken")
    public R qiniuToken() {
        String accessKey = "QGwnW_OYnSO3QR2osJ2mwqManZECHvFZ1tPq8bAv";
        String secretKey = "xQzpa36UhxHWj6PXHgNJJeGiFQifbOxVLDZ5zWue";
        String bucket = "avatar";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket, null, 3600, new StringMap()
                .put("returnBody", " {\"key\": $(key), \"hash\": $(etag), \"w\": $(imageInfo.width), \"h\": $(imageInfo.height)}"));
        return R.renderSuccess("uptoken", upToken);
    }

    @PostMapping("login")
    public R login(@RequestParam Map<String, String> map) {
//        try {
//            WxMaJscode2SessionResult wxMaJscode2SessionResult = wxMaUserService.getSessionInfo(map.get("code"));
//            WxMaUserInfo wxMaUserInfo = wxMaUserService.getUserInfo(wxMaJscode2SessionResult.getSessionKey(), map.get("encryptedData"), map.get("iv"));
//            SysUser sysUser = new SysUser();
//            sysUser.setOpenid(wxMaUserInfo.getOpenId());
//            sysUser = sysUserMapper.selectOne(sysUser);
//            if (sysUser != null) {
//                sysUser.setGender(Integer.valueOf(wxMaUserInfo.getGender()));
//                sysUser.setAvatarurl(wxMaUserInfo.getAvatarUrl());
//                sysUserMapper.updateByPrimaryKeySelective(sysUser);
//            } else {
//                sysUser = new SysUser();
//                sysUser.setAvatarurl(wxMaUserInfo.getAvatarUrl());
//                sysUser.setNickname(wxMaUserInfo.getNickName());
//                sysUser.setCity(wxMaUserInfo.getCity());
//                sysUser.setProvince(wxMaUserInfo.getProvince());
//                sysUser.setCountry(wxMaUserInfo.getCountry());
//                sysUser.setLanguage(wxMaUserInfo.getLanguage());
//                sysUser.setGender(Integer.valueOf(wxMaUserInfo.getGender()));
//                sysUser.setOpenid(wxMaUserInfo.getOpenId());
//                sysUserMapper.insertSelective(sysUser);
//            }
//            return R.renderSuccess("token", token);
//        } catch (WxErrorException e) {
//            e.printStackTrace();
//        }
        return R.error();
    }
}
