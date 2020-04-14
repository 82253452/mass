package com.f4w.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.f4w.entity.Wxmp;
import com.f4w.mapper.BusiAppMapper;
import com.f4w.mapper.WxmpMapper;
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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
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
            list.forEach(e -> {
                wxmpMapper.deleteById(e.getId());
                if (StringUtils.isBlank(e.getThumbnail())) {
                    log.error("图片为空---", e.getId());
                    return;
                }
                WxMpMaterialNews.WxMpMaterialNewsArticle news = new WxMpMaterialNews.WxMpMaterialNewsArticle();
                news.setTitle(e.getTitle());
                try {
                    news.setThumbMediaId(uploadFile(appId, e.getThumbnail()));
                } catch (IOException | WxErrorException ex) {
                    log.error("图片上传失败---", e.getThumbnail(), "---", ex.getMessage());
                    return;
                }
                news.setAuthor(e.getAuther());
                news.setContent(getContent(appId, type, e));
                news.setDigest(e.getSummary());
                if (comment != null && comment) {
                    news.setNeedOpenComment(true);
                }
                newsList.add(news);
            });

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

    private String getContent(String appId, String type, Wxmp e) {
        if (StringUtils.equals(type, "0")) {
            return "<iframe frameborder=\"0\" width=\"640\" height=\"498\" src=\"https://v.qq.com/iframe/player.html?vid=" + e.getVideoId() + "&tiny=0&auto=0\" allowfullscreen></iframe>";
        } else if (StringUtils.equals(type, "1")) {
            return dealContent(appId, e.getContent());
        }
        return e.getContent();
    }

    private String dealContent(String appId, String content) {
        Document doc = Jsoup.parse(content);
        Element body = doc.body();
        //处理 image
        Elements src = body.getElementsByAttribute("src");
        src.forEach(s -> {
            s.attr("src", imageUpload(appId, s.attr("src")));
        });
        return content;
    }


    private String uploadFile(String appId, String thumbnail) throws IOException, WxErrorException {
        File file = File.createTempFile(UUID.randomUUID().toString(), ".png");
        URL url = new URL(thumbnail);
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

    private static void imgScale(File file, double size) throws IOException {
        //判断大小，如果小于指定大小，不压缩；如果大于等于指定大小，压缩
        if (file.length() <= size * 1024 * 1024) {
            return;
        }
        //按照比例进行缩小
        Thumbnails.of(file).scale(0.9).toFile(file);//按比例缩小
        imgScale(file, size);
    }

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.parse("<span><span><p ><img src=\"http://image.uc.cn/s/wemedia/s/upload/2020/32da4a7897af7e515fe78e2f1333f885.png\" /></p></span></span><span><p>首先毋庸置疑的是，war3重置版虽然褒贬不一，甚至很多玩家不买账，但的的确确为war3提供了一波人气，如果暴雪后面能真的推出让人信服的正式版，没准会有真香，但这种事现在都不好说。不过，war3到底还能走多久？</p></span><span><span><p ><img src=\"http://image.uc.cn/s/wemedia/s/upload/2020/6c6d675dd1bbc94f00fae38212cf7192.png\" /></p></span></span><span><p>就看比赛而言，几乎全都是老一辈的人，moon，happy，infi，三蛋等等，我在想，如果这一批人真的老去了，真的就提不动刀了，留给他们还有多长时间？我约莫着是五年，最长十年，首先五年左右，80后的一线选手率先提不起刀，十年左右，90后的一批人会提不起刀。</p><p>这不一定是好事，也不一定是坏事，或许如果moon某一天宣布彻底退役会让我们的青春结束，那么happy，infi，TH000，lyn、fly等人如果逐渐也都退役，是不是我们的青春是一次又一次的结束？等老的一批真的都退役了，首先，war3选手的水平并不会有断崖式的下降。</p><p>因为网易已经提暴雪，把魔兽争霸3玩起来了，rpg地图的售卖让网易赚了不少钱，然后反哺war3，搞比赛，奖金说实话不低，这肯定能吸引不少俱乐部的注意，因为war3项目对于一个俱乐部而言，是单人项目，不是5v5，6v6的，只需要养一个或者两个就够了。</p></span><span><span><p ><img src=\"http://image.uc.cn/s/wemedia/s/upload/2020/0052776cfccea6715d25fa9daf733214.png\" /></p></span></span><span><p>其实从13年左右，就一直有一种声音就是war3已死，war3已死，实际上七年过去了，war3依然活得好好的，比赛依然有，观看人数依然不输大部分游戏。要知道rpg是零门槛的，跟如今的新游戏一样，上手很快，玩法又独特，总能吸引新鲜血液，只要这批新鲜血液能记住魔兽争霸，就会去看比赛，就会对war3本身感兴趣。</p><p>所以，我认为war3如今保持这种良性循环，就一定可以继续走下去，长久不衰。</p></span>,,");
        Element body = doc.body();
        //处理 image
        Elements src = body.getElementsByAttribute("src");
        src.forEach(s -> {
            System.out.println(s.toString());
        });
    }


    @SneakyThrows
    private String imageUpload(String appId, String thumbnail) {
        File file = null;
        try {
            file = File.createTempFile(UUID.randomUUID().toString(), ".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        URL url = new URL(thumbnail);
        ImageIO.write(ImageIO.read(url), "png", file);
        imgScale(file, 0.8);
        WxMpMaterial wxMpMaterial = new WxMpMaterial();
        wxMpMaterial.setFile(file);
        wxMpMaterial.setName("media");
        WxMediaImgUploadResult result = wxOpenService
                .getWxOpenComponentService()
                .getWxMpServiceByAppid(appId)
                .getMaterialService()
                .mediaImgUpload(file);
        return result.getUrl();
    }


}
