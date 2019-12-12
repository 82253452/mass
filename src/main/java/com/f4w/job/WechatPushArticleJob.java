package com.f4w.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.f4w.entity.BusiApp;
import com.f4w.entity.Wxmp;
import com.f4w.mapper.BusiAppMapper;
import com.f4w.mapper.WxmpMapper;
import com.f4w.weapp.WxOpenService;
import com.github.pagehelper.PageHelper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        JSONObject o = JSON.parseObject(s);
        String appId = o.getString("appId");
//        Integer num = o.getInteger("num");
//        Integer type = o.getInteger("type");
        Integer column = o.getInteger("column");
        Boolean comment = o.getBoolean("comment");
        String types = o.getString("types");
//        Wxmp wxmp = new Wxmp();
        //        wxmp.setType(type);
//        wxmp.setColumnId(column);
        if (StringUtils.isBlank(types)) {
            log.error("没有要发送的图文");
            return new ReturnT<>("ok");
        }
        String[] typesArray = types.split("-");
        if (ArrayUtils.isEmpty(typesArray)) {
            log.error("没有要发送的图文");
            return new ReturnT<>("ok");
        }
        List<WxMpMaterialNews.WxMpMaterialNewsArticle> newsList = new ArrayList<>();
        for (String type : typesArray) {
            Example example = new Example(Wxmp.class);
            example.setOrderByClause("id DESC");
            example.createCriteria()
                    .andEqualTo("type", type)
                    .andEqualTo("del", "0")
                    .andEqualTo("columnId", column);

            PageHelper.startPage(1, 1);
            List<Wxmp> list = wxmpMapper.selectByExample(example);
            try {
                list.forEach(e -> {
                    try {
                        wxmpMapper.deleteById(e.getId());
                        WxMpMaterialNews.WxMpMaterialNewsArticle news = new WxMpMaterialNews.WxMpMaterialNewsArticle();
                        news.setTitle(e.getTitle());
                        File file = File.createTempFile(UUID.randomUUID().toString(), ".png");
                        String thumbnail = e.getThumbnail();
                        if (StringUtils.isNotBlank(thumbnail)) {
                            URL url = new URL(thumbnail);
                            BufferedImage img = ImageIO.read(url);
                            ImageIO.write(img, "png", file);
                            WxMpMaterial wxMpMaterial = new WxMpMaterial();
                            wxMpMaterial.setFile(file);
                            wxMpMaterial.setName("media");
                            WxMpMaterialUploadResult result = wxOpenService
                                    .getWxOpenComponentService()
                                    .getWxMpServiceByAppid(appId)
                                    .getMaterialService()
                                    .materialFileUpload("image", wxMpMaterial);
                            String mediaId = result.getMediaId();
                            news.setThumbMediaId(mediaId);
                            news.setAuthor(e.getAuther());
                            if (StringUtils.equals(type, "0")) {
                                news.setContent("<iframe frameborder=\"0\" width=\"640\" height=\"498\" src=\"https://v.qq.com/iframe/player.html?vid=" + e.getVideoId() + "&tiny=0&auto=0\" allowfullscreen></iframe>");
                            } else if (StringUtils.equals(type, "1")) {
                                news.setContent(e.getContent());
                            } else {
                                return;
                            }
                            news.setDigest(e.getSummary());
                            if (comment != null && comment) {
                                news.setNeedOpenComment(true);
                            }
                            newsList.add(news);
                        }
                        file.deleteOnExit();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        WxMpMaterialNews wxMpMaterialNews = new WxMpMaterialNews();
        wxMpMaterialNews.setArticles(newsList);
        WxMpMaterialUploadResult re = wxOpenService
                .getWxOpenComponentService()
                .getWxMpServiceByAppid(appId)
                .getMaterialService().materialNewsUpload(wxMpMaterialNews);
        WxMpMassTagMessage wxMpMassTagMessage = new WxMpMassTagMessage();
        wxMpMassTagMessage.setSendAll(true);
        wxMpMassTagMessage.setMediaId(re.getMediaId());
        wxMpMassTagMessage.setMsgType("mpnews");
        wxMpMassTagMessage.setSendIgnoreReprint(true);
        wxOpenService.getWxOpenComponentService()
                .getWxMpServiceByAppid(appId)
                .getMassMessageService()
                .massGroupMessageSend(wxMpMassTagMessage);

        return new ReturnT<>("ok");
    }
}
