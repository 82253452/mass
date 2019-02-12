package com.f4w.job;

import com.f4w.mapper.WxmpMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@JobHandler(value = "removeDuplicatesJobHandler")
@Service
public class RemoveDuplicatesJob extends IJobHandler {
    @Resource
    private WxmpMapper wxmpMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        wxmpMapper.deleteDuplicates();
        return new ReturnT<>("ok");
    }
}
