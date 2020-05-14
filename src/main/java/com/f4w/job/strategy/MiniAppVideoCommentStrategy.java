package com.f4w.job.strategy;

import com.f4w.dto.annotation.ArticleType;
import com.f4w.dto.enums.ArticleTypeEnum;
import com.f4w.dto.req.JobInfoReq;
import com.f4w.entity.BusiApp;
import com.f4w.entity.Wxmp;
import com.f4w.job.CommentStrategy;
import com.f4w.utils.JobException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@ArticleType(ArticleTypeEnum.MINIAPP_VIDEO)
public class MiniAppVideoCommentStrategy extends CommentStrategy {
    @Override
    public String dealHtml(JobInfoReq jobinfo, BusiApp busiApp, Wxmp wxmp) throws JobException {
        String img = imageCoverUpload(busiApp.getAppId(), wxmp.getThumbnail());
       return "<p>" +
               "<a class=\"weapp_text_link\"" +
               " style=\"font-size:17px;display:block;width:100%!important;" +
               "height: 280px!important;\n" +
               "    display:block;\n" +
               "    background-color: #f7f7f7;\n" +
               "    border-radius: 8px;" +
               "    padding:12px 12px 8px;\n" +
               "    background-image:url("+img+");\n" +
               "    background-repeat: no-repeat;\n" +
               "    background-size: 100% 100%;\n" +
               "    background-position: 0px 50px;\n" +
               "    color: #888;" +
               "\" " +
               "data-miniprogram-appid=\""+jobinfo.getMiniAppId()+"\" " +
               "data-miniprogram-path=\""+replaceStr(jobinfo.getMiniAppPath(),wxmp.getVideoId())+"\" " +
               "data-miniprogram-nickname=\""+busiApp.getNickName()+"\" href=\"\" " +
               "data-miniprogram-type=\"text\" " +
               "data-miniprogram-servicetype=\"\">" +
               busiApp.getNickName() +
               "</a>" ;
    }

    @Override
    public Wxmp findWxmp(JobInfoReq jobinfo) throws JobException {
        Wxmp wxmp = Optional.ofNullable(wxmpMapper.findWxmpByType(0, jobinfo.getColumn())).orElseThrow(() -> new JobException("查询视频为空" + jobinfo.getColumn()));
        return wxmp;
    }

}
