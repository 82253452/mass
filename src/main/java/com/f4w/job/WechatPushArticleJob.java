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
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
    public ReturnT<String> execute(String s) {
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
                        String thumbnail = e.getThumbnail();
                        if (StringUtils.isNotBlank(thumbnail)) {
                            news.setThumbMediaId(uploadFile(appId,  thumbnail));
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
        WxMpMaterialUploadResult re = null;
        try {
            re = wxOpenService
                    .getWxOpenComponentService()
                    .getWxMpServiceByAppid(appId)
                    .getMaterialService().materialNewsUpload(wxMpMaterialNews);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return new ReturnT<>("上传图片失败");
        }
        WxMpMassTagMessage wxMpMassTagMessage = new WxMpMassTagMessage();
        wxMpMassTagMessage.setSendAll(true);
        wxMpMassTagMessage.setMediaId(re.getMediaId());
        wxMpMassTagMessage.setMsgType("mpnews");
        wxMpMassTagMessage.setSendIgnoreReprint(true);
        try {
            wxOpenService.getWxOpenComponentService()
                    .getWxMpServiceByAppid(appId)
                    .getMassMessageService()
                    .massGroupMessageSend(wxMpMassTagMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return new ReturnT<>("上传文章失败");
        }

        return new ReturnT<>("ok");
    }

    private String uploadFile(String appId, String thumbnail) throws IOException, WxErrorException {
        File file = File.createTempFile(UUID.randomUUID().toString(), ".png");
        URL url = new URL("https://kan-jian.oss-cn-beijing.aliyuncs.com/topic/20200214/20200214181318_y3td.png");
        ImageIO.write(ImageIO.read(url),"png",file);
        imgScale(file);
        WxMpMaterial wxMpMaterial = new WxMpMaterial();
        wxMpMaterial.setFile(file);
        wxMpMaterial.setName("media");
        WxMpMaterialUploadResult result = wxOpenService
                .getWxOpenComponentService()
                .getWxMpServiceByAppid(appId)
                .getMaterialService()
                .materialFileUpload("image", wxMpMaterial);
        file.deleteOnExit();
        return  result.getMediaId();

    }

    private static void imgScale(File file) throws IOException{
        //判断大小，如果小于指定大小，不压缩；如果大于等于指定大小，压缩
        if(file.length()<=2.8*1024*1024){
            return;
        }
        //按照比例进行缩小
        Thumbnails.of(file).scale(0.9).toFile(file);//按比例缩小
        imgScale(file);
    }


}
