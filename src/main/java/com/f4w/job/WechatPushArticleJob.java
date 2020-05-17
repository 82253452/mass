package com.f4w.job;

import com.alibaba.fastjson.JSON;
import com.f4w.dto.req.JobInfoReq;
import com.f4w.entity.BusiApp;
import com.f4w.entity.Wxmp;
import com.f4w.mapper.BusiAppMapper;
import com.f4w.mapper.WxmpMapper;
import com.f4w.utils.DingWarning;
import com.f4w.utils.JobException;
import com.f4w.utils.ValidateUtils;
import com.f4w.weapp.WxOpenService;
import com.github.pagehelper.PageHelper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
import me.chanjar.weixin.mp.bean.material.WxMediaImgUploadResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

@Slf4j
@JobHandler(value = "weArticleJobHandler")
@Service
public class WechatPushArticleJob extends IJobHandler {
    @Resource
    private BusiAppMapper busiAppMapper;
    @Resource
    private WxOpenService wxOpenService;
    @Resource
    private WxmpMapper wxmpMapper;
    @Resource
    private CommentContext commentContext;

    @Override
    @Transactional
    public ReturnT<String> execute(String s) throws JobException {
        log.info("群发素材--" + s);
        JobInfoReq jobinfo = JSON.parseObject(s, JobInfoReq.class);
        //校验
        ValidateUtils.validateThrowsJobException(jobinfo);
        //数据库查找素材
        List<WxMpMaterialNews.WxMpMaterialNewsArticle> newsList = addNewsList(jobinfo);
        //上传素材到微信
        String mediaId = uploadArticles(jobinfo, newsList);
        //发布文章
        pushMedias(jobinfo, mediaId);
        return new ReturnT<>("ok");
    }

    private void pushMedias(JobInfoReq jobinfo, String mediaId) throws JobException {
        //不群发消息
        if (BooleanUtils.isNotTrue(jobinfo.getIsPush())) {
            return;
        }
        pushMedias(jobinfo, mediaId);
        WxMpMassTagMessage wxMpMassTagMessage = new WxMpMassTagMessage();
        wxMpMassTagMessage.setSendAll(true);
        wxMpMassTagMessage.setMediaId(mediaId);
        wxMpMassTagMessage.setMsgType("mpnews");
        wxMpMassTagMessage.setSendIgnoreReprint(true);
        try {
            wxOpenService.getWxOpenComponentService()
                    .getWxMpServiceByAppid(jobinfo.getAppId())
                    .getMassMessageService()
                    .massGroupMessageSend(wxMpMassTagMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new JobException("发布文章失败");
        }
    }

    private String uploadArticles(JobInfoReq jobinfo, List<WxMpMaterialNews.WxMpMaterialNewsArticle> newsList) throws JobException {
        WxMpMaterialNews wxMpMaterialNews = new WxMpMaterialNews();
        wxMpMaterialNews.setArticles(newsList);
        WxMpMaterialUploadResult re;
        try {
            re = wxOpenService
                    .getWxOpenComponentService()
                    .getWxMpServiceByAppid(jobinfo.getAppId())
                    .getMaterialService().materialNewsUpload(wxMpMaterialNews);
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new JobException("上传素材失败");
        }
        return re.getMediaId();
    }

    private List<WxMpMaterialNews.WxMpMaterialNewsArticle> addNewsList(JobInfoReq jobinfo) throws JobException {
        BusiApp busiApp = Optional.ofNullable(busiAppMapper.selectOne(BusiApp.builder().appId(jobinfo.getAppId()).build())).orElseThrow(() -> new JobException("appid 错误"));
        List<WxMpMaterialNews.WxMpMaterialNewsArticle> newsList = new ArrayList<>();
        for (String type : jobinfo.getTypes().split("-")) {
            addWxArticle(jobinfo, busiApp, newsList, Integer.parseInt(type));
        }
        if (CollectionUtils.isEmpty(newsList)) {
            DingWarning.log("数据处理失败素材为空-{}", jobinfo.getAppId());
            throw new JobException("数据处理失败素材为空");
        }
        return newsList;
    }


    private void addWxArticle(JobInfoReq jobinfo, BusiApp busiApp, List<WxMpMaterialNews.WxMpMaterialNewsArticle> newsList, Integer type) throws JobException {
        Wxmp e = commentContext.getCommentStrategy(type).findWxmp(jobinfo);
        wxmpMapper.deleteById(e.getId());
        if (StringUtils.isBlank(e.getThumbnail())) {
            log.error("图片为空---{}", e.getId());
            addWxArticle(jobinfo, busiApp, newsList, type);
        }
        WxMpMaterialNews.WxMpMaterialNewsArticle news = new WxMpMaterialNews.WxMpMaterialNewsArticle();
        news.setTitle(e.getTitle());
        try {
            news.setThumbMediaId(uploadFile(jobinfo.getAppId(), e.getThumbnail()));
        } catch (NullPointerException | IOException | WxErrorException ex) {
            log.error("图片上传失败---{}--{}--¬", e.getThumbnail(), ex.getMessage());
            DingWarning.log("图片上传失败-{}-{}-", e.getThumbnail(), ex.getMessage());
            addWxArticle(jobinfo, busiApp, newsList, type);
        }
        news.setAuthor(StringUtils.isBlank(e.getAuther()) ? "原创达人" : e.getAuther());
        news.setDigest(e.getSummary());
        news.setContent(commentContext.getCommentStrategy(type).dealHeadAndFooter(jobinfo, busiApp, e));
        if (StringUtils.isNotBlank(jobinfo.getContentSourceUrl())) {
            news.setContentSourceUrl(jobinfo.getContentSourceUrl());
        }
        if (jobinfo.getComment() != null && jobinfo.getComment()) {
            news.setNeedOpenComment(true);
        }
        newsList.add(news);
    }

    public String uploadFile(String appId, String thumbnail) throws IOException, WxErrorException {
        File file = File.createTempFile(UUID.randomUUID().toString(), ".png");
        URL url = new URL(thumbnail);
        BufferedImage read = ImageIO.read(url);
        if (read == null) {
            throw new NullPointerException("图片异常");
        }
        ImageIO.write(ImageIO.read(url), "png", file);
        imgScale(file, 1.8);
        WxMpMaterial wxMpMaterial = new WxMpMaterial();
        wxMpMaterial.setFile(file);
        wxMpMaterial.setName("media");
        WxMpMaterialUploadResult result = wxOpenService
                .getWxOpenComponentService()
                .getWxMpServiceByAppid(appId)
                .getMaterialService()
                .materialFileUpload("image", wxMpMaterial);
        file.deleteOnExit();
        return result.getMediaId();
    }

    public static void imgScale(File file, double size) throws IOException {
        //判断大小，如果小于指定大小，不压缩；如果大于等于指定大小，压缩
        if (file.length() <= size * 1024 * 1024) {
            return;
        }
        //按照比例进行缩小
        Thumbnails.of(file).scale(0.9).toFile(file);//按比例缩小
        imgScale(file, size);
    }


}
