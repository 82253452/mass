package com.f4w.weapp;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.open.api.impl.WxOpenInMemoryConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenMessageRouter;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
@Slf4j
@EnableConfigurationProperties({WeiXinOpenConfig.class})
public class WxOpenService extends WxOpenServiceImpl {
    @Resource
    private WeiXinOpenConfig weiXinOpenConfig;

    @PostConstruct
    public void init() {
        WxOpenInMemoryConfigStorage wxOpenInMemoryConfigStorage = new WxOpenInMemoryConfigStorage();
        wxOpenInMemoryConfigStorage.setComponentAppId(weiXinOpenConfig.getComponentAppId());
        wxOpenInMemoryConfigStorage.setComponentAppSecret(weiXinOpenConfig.getComponentSecret());
        wxOpenInMemoryConfigStorage.setComponentToken(weiXinOpenConfig.getComponentToken());
        wxOpenInMemoryConfigStorage.setComponentAesKey(weiXinOpenConfig.getComponentAesKey());
        setWxOpenConfigStorage(wxOpenInMemoryConfigStorage);
    }

    @Bean
    public WxOpenMessageRouter wxOpenMessageRouter() {
        WxOpenMessageRouter wxOpenMessageRouter = new WxOpenMessageRouter(this);
        wxOpenMessageRouter.rule().handler((wxMpXmlMessage, map, wxMpService, wxSessionManager) -> {
            log.info("\n接收到 {} 公众号请求消息，内容：{}", wxMpService.getWxMpConfigStorage().getAppId(), wxMpXmlMessage);
            return null;
        }).next();
        return wxOpenMessageRouter;
    }

}
