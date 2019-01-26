package com.f4w.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@JobHandler(value = "testJobHandler")
@Service
public class wechatPushArticleJob extends IJobHandler {
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("12312312");
        System.out.println("222222222");
        return new ReturnT<>("ok");
    }
}
