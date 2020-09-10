package com.f4w.service;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.f4w.dto.req.MiniAppLoginReq;
import com.f4w.entity.BusiApp;
import com.f4w.entity.SysUser;
import com.f4w.job.WechatPushArticleJob;
import com.f4w.mapper.SysUserMapper;
import com.f4w.utils.ForeseenException;
import com.f4w.utils.JWTUtils;
import com.f4w.utils.ShowException;
import com.f4w.weapp.WxOpenService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class WechatService {
    @Resource
    private WechatPushArticleJob wechatPushArticleJob;
    @Resource
    private WxOpenService wxOpenService;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private JWTUtils jwtUtils;

    @Async
    public void pushArticle(BusiApp busiApp) {
        wechatPushArticleJob.execute(busiApp.getMessageParam());
    }

    public SysUser loginFromOpenThird(MiniAppLoginReq miniAppLoginDto) throws ForeseenException {
        WxMaJscode2SessionResult session;
        try {
            session = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(miniAppLoginDto.getAppId()).getUserService().getSessionInfo(miniAppLoginDto.getCode());
        } catch (WxErrorException e) {
            log.error("用户授权失败", e);
            throw new ForeseenException(e.getMessage());
        }
        // 解密用户信息
        WxMaUserInfo userInfo = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(miniAppLoginDto.getAppId()).getUserService().getUserInfo(session.getSessionKey(), miniAppLoginDto.getEncryptedData(), miniAppLoginDto.getIv());
        SysUser sysUser = sysUserMapper.selectOne(SysUser.builder().unionid(userInfo.getUnionId()).build());
        if (sysUser == null) {
            sysUser = SysUser.builder()
                    .openid(userInfo.getOpenId())
                    .nickname(userInfo.getNickName())
                    .gender(Integer.valueOf(userInfo.getGender()))
                    .language(userInfo.getLanguage())
                    .city(userInfo.getCity())
                    .province(userInfo.getProvince())
                    .country(userInfo.getCountry())
                    .avatarurl(userInfo.getAvatarUrl())
                    .unionid(userInfo.getUnionId())
                    .appId(miniAppLoginDto.getAppId())
                    .userName(userInfo.getNickName())
                    .build();
            sysUserMapper.insertSelective(sysUser);
        }
        jwtUtils.creatKey(sysUser);
        return sysUser;
    }

    public SysUser getUserInfoByCode(String appId, String code) throws ForeseenException {
        WxMaJscode2SessionResult session;
        try {
            session = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).getUserService().getSessionInfo(code);
            log.info(session.getUnionid());
        } catch (WxErrorException e) {
            log.error("用户授权失败", e);
            throw new ForeseenException(e.getMessage());
        }
        if (session.getUnionid() == null) {
            return null;
        }
        SysUser sysUser = sysUserMapper.selectOne(SysUser.builder().unionid(session.getUnionid()).build());
        return sysUser;
    }

    public String getPhoneNoInfo(MiniAppLoginReq request) throws ForeseenException {
        WxMaJscode2SessionResult session;
        try {
            session = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(request.getAppId()).getUserService().getSessionInfo(request.getCode());
        } catch (WxErrorException e) {
            log.error("用户授权失败", e);
            throw new ForeseenException(e.getMessage());
        }
        if (session == null) {
            throw new ShowException("获取手机号失败");
        }
        String sessionKey = session.getSessionKey();
        WxMaPhoneNumberInfo phoneNoInfo = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(request.getAppId()).getUserService().getPhoneNoInfo(sessionKey, request.getEncryptedData(), request.getIv());
        if (phoneNoInfo == null) {
            throw new ShowException("获取手机号失败");
        }
        return phoneNoInfo.getPhoneNumber();
    }
}
