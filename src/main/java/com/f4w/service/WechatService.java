package com.f4w.service;

import com.f4w.entity.BusiApp;
import com.f4w.job.WechatPushArticleJob;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WechatService {
    @Resource
    private WechatPushArticleJob wechatPushArticleJob;
    @Async
    public void pushArticle(BusiApp busiApp) {
        wechatPushArticleJob.execute(busiApp.getMessageParam());
    }
}
