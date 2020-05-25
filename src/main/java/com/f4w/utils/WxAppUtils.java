//package com.f4w.utils;
//
//import cn.binarywang.wx.miniapp.api.WxMaService;
//import cn.binarywang.wx.miniapp.api.WxMaUserService;
//import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
//import cn.binarywang.wx.miniapp.api.impl.WxMaUserServiceImpl;
//import cn.binarywang.wx.miniapp.config.WxMaConfig;
//import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
//import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
//import cn.binarywang.wx.miniapp.config.impl.WxMaRedisBetterConfigImpl;
//import com.f4w.entity.BusiApp;
//
//public class WxAppUtils {
//    public static WxMaUserService getWxMaUserService(BusiApp busiApp) {
////        WxMaConfig config = new WxMaRedisBetterConfigImpl();
//////        WxMaInMemoryConfig config = new WxMaInMemoryConfig();
////        config.setAppid(busiApp.getAppId());
//////        config.setSecret(busiApp.getAppSecret());
////        WxMaService service = new WxMaServiceImpl();
////        service.setWxMaConfig(config);
//        return new WxMaUserServiceImpl(service);
//    }
//}
