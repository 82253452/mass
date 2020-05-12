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
        List<WxMpMaterialNews.WxMpMaterialNewsArticle> newsList = new ArrayList<>();
        for (String type : jobinfo.getTypes().split("-")) {
            addWxArticle(jobinfo, newsList, Integer.parseInt(type) % 2, findTypeData(Integer.parseInt(type) % 2, Integer.parseInt(type) > 1 ? "1" : "0", jobinfo.getColumn()));
        }
        if (CollectionUtils.isEmpty(newsList)) {
            DingWarning.log("数据处理失败素材为空-{}", jobinfo.getAppId());
            throw new JobException("数据处理失败素材为空");
        }
        return newsList;
    }

    private Wxmp findTypeData(Integer type, String isTop, Integer columnId) throws JobException {
        Example example = new Example(Wxmp.class);
        example.setOrderByClause("id DESC");
        example.createCriteria()
                .andEqualTo("type", type)
                .andEqualTo("del", "0")
                .andEqualTo("isTop", isTop)
                .andEqualTo("columnId", columnId);
        PageHelper.startPage(1, 1);
        List<Wxmp> list = wxmpMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list) && StringUtils.equals("1", isTop)) {
            return findTypeData(type, "0", columnId);
        }
        if (CollectionUtils.isEmpty(list) && StringUtils.equals("0", isTop)) {
            DingWarning.log("列表数据为空-{}", type.toString());
            throw new JobException("列表数据为空");
        }
        return list.get(0);
    }

    private void addWxArticle(JobInfoReq jobinfo, List<WxMpMaterialNews.WxMpMaterialNewsArticle> newsList, Integer type, Wxmp e) throws JobException {
        wxmpMapper.deleteById(e.getId());
        if (StringUtils.isBlank(e.getThumbnail())) {
            log.error("图片为空---{}", e.getId());
            return;
        }
        WxMpMaterialNews.WxMpMaterialNewsArticle news = new WxMpMaterialNews.WxMpMaterialNewsArticle();
        news.setTitle(e.getTitle());
        try {
            news.setThumbMediaId(uploadFile(jobinfo.getAppId(), e.getThumbnail()));
        } catch (IOException | WxErrorException ex) {
            log.error("图片上传失败---{}--{}--¬", e.getThumbnail(), ex.getMessage());
            DingWarning.log("图片上传失败-{}-{}-", e.getThumbnail(), ex.getMessage());
            return;
        }
        news.setAuthor(e.getAuther());
        news.setDigest(e.getSummary());
        news.setContent(getContent(jobinfo.getAppId(), type, e, news));
        if (StringUtils.isNotBlank(jobinfo.getContentSourceUrl())) {
            news.setContentSourceUrl(jobinfo.getContentSourceUrl());
        }
        if (jobinfo.getComment() != null && jobinfo.getComment()) {
            news.setNeedOpenComment(true);
        }
        newsList.add(news);
    }

    private String getContent(String appId, Integer type, Wxmp e, WxMpMaterialNews.WxMpMaterialNewsArticle news) throws JobException {
        if (0 == type) {
            return "<iframe frameborder=\"0\" width=\"640\" height=\"498\" src=\"https://v.qq.com/iframe/player.html?vid=" + e.getVideoId() + "&tiny=0&auto=0\" allowfullscreen></iframe>";
        } else if (1 == type) {
            return dealContent(appId, e, news);
        }
        return e.getContent();
    }

    private String dealContent(String appId, Wxmp wxmp, WxMpMaterialNews.WxMpMaterialNewsArticle news) throws JobException {
        Document doc = Jsoup.parse(wxmp.getContent());
        Element body = doc.body();
        //处理 image
        Elements src = body.getElementsByAttribute("src");
        src.forEach(s -> {
            s.attr("src", imageUpload(appId, s.attr("src")));
        });
        //增加头部获取尾部元素
        BusiApp busiApp = Optional.ofNullable(busiAppMapper.selectOne(BusiApp.builder().appId(appId).build())).orElseThrow(() -> new JobException("查询appId 失败--" + appId));
        if (StringUtils.isNotBlank(busiApp.getHeaderText())) {
            body.prepend(busiApp.getHeaderText());
        }
        if (StringUtils.isNotBlank(busiApp.getFooterText())) {
            body.append(busiApp.getFooterText());
        }
        //处理小程序
        Elements miniApp = body.select(".my-custem-appId");
        miniApp.forEach(e -> {
            String miniAppId = e.text();
            e.attr("style", "");
            e.html("<a class=\"weapp_image_link\" data-miniprogram-appid=\"" + miniAppId + "\" data-miniprogram-path=\"pages/index/index_0\" " +
                    "data-miniprogram-nickname=\"" + wxmp.getTitle() + "\" href=\"\" data-miniprogram-type=\"image\" data-miniprogram-servicetype=\"\">" +
                    "<img class=\"rich_pages \" data-ratio=\"0.5260029717682021\" data-s=\"300,640\" " +
                    "data-type=\"jpeg\" data-w=\"1346\" style=\"width: 677px !important; height: auto !important; visibility: visible !important;\" _width=\"677px\" " +
                    "src=\"" + imageCoverUpload(appId, wxmp.getThumbnail()) + "\" " +
                    "crossorigin=\"anonymous\" data-fail=\"0\" title=\"\"></a>");
        });
        return body.html();
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
        Document doc = Jsoup.parse("<p class=\"my-custem-appId\" style=\"width: 300px; height: 150px; line-height: 150px; text-align: center; box-shadow: 0 1px 3px 0 rgba(0, 0, 0, .5);\">123</p>\n" +
                "<p>12321</p><span><span><p ><img src=\"http://image.uc.cn/s/wemedia/s/upload/2020/32da4a7897af7e515fe78e2f1333f885.png\" /></p></span></span><span><p>首先毋庸置疑的是，war3重置版虽然褒贬不一，甚至很多玩家不买账，但的的确确为war3提供了一波人气，如果暴雪后面能真的推出让人信服的正式版，没准会有真香，但这种事现在都不好说。不过，war3到底还能走多久？</p></span><span><span><p ><img src=\"http://image.uc.cn/s/wemedia/s/upload/2020/6c6d675dd1bbc94f00fae38212cf7192.png\" /></p></span></span><span><p>就看比赛而言，几乎全都是老一辈的人，moon，happy，infi，三蛋等等，我在想，如果这一批人真的老去了，真的就提不动刀了，留给他们还有多长时间？我约莫着是五年，最长十年，首先五年左右，80后的一线选手率先提不起刀，十年左右，90后的一批人会提不起刀。</p><p>这不一定是好事，也不一定是坏事，或许如果moon某一天宣布彻底退役会让我们的青春结束，那么happy，infi，TH000，lyn、fly等人如果逐渐也都退役，是不是我们的青春是一次又一次的结束？等老的一批真的都退役了，首先，war3选手的水平并不会有断崖式的下降。</p><p>因为网易已经提暴雪，把魔兽争霸3玩起来了，rpg地图的售卖让网易赚了不少钱，然后反哺war3，搞比赛，奖金说实话不低，这肯定能吸引不少俱乐部的注意，因为war3项目对于一个俱乐部而言，是单人项目，不是5v5，6v6的，只需要养一个或者两个就够了。</p></span><span><span><p ><img src=\"http://image.uc.cn/s/wemedia/s/upload/2020/0052776cfccea6715d25fa9daf733214.png\" /></p></span></span><span><p>其实从13年左右，就一直有一种声音就是war3已死，war3已死，实际上七年过去了，war3依然活得好好的，比赛依然有，观看人数依然不输大部分游戏。要知道rpg是零门槛的，跟如今的新游戏一样，上手很快，玩法又独特，总能吸引新鲜血液，只要这批新鲜血液能记住魔兽争霸，就会去看比赛，就会对war3本身感兴趣。</p><p>所以，我认为war3如今保持这种良性循环，就一定可以继续走下去，长久不衰。</p></span>,,");
        Element body = doc.body();
        //处理 image
//        Elements src = body.getElementsByAttribute("src");
//        src.forEach(s -> {
//            System.out.println(s.toString());
//        });
        Elements miniApp = body.select(".my-custem-appId");
        miniApp.forEach(e -> {
            String appId = e.text();
            System.out.println(appId);
            e.html("<img></img>");
            e.attr("style", "");
        });
        System.out.println(body.html());
    }

    /**
     * 图片合成播放按钮
     */
    @SneakyThrows
    private void playCoverImg(BufferedImage background) {
        URL url = new URL("https://kan-jian.oss-cn-beijing.aliyuncs.com/topic/20200512/20200512181329_egsp.png");
        BufferedImage play = ImageIO.read(url);
        int playWidth = play.getWidth();
        int playHeight = play.getHeight();

        Graphics2D bgG2 = (Graphics2D) background.getGraphics();
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();

        int startX = (bgWidth - playWidth) / 2;
        int startY = (bgHeight - playHeight) / 2;
        bgG2.drawImage(play, startX, startY, playWidth, playHeight, null);
        bgG2.dispose();
    }

    /**
     * 图片遮罩
     *
     * @param background
     * @throws IOException
     */
    private void coverImg(BufferedImage background) throws IOException {
        Graphics2D bgG2 = (Graphics2D) background.getGraphics();

        //遮罩层大小
        int coverWidth = background.getWidth();
        int coverHeight = background.getHeight();
        //遮罩层位置
        int coverX = 0;
        int coverY = background.getHeight() - coverHeight;

        //创建黑色遮罩层
        BufferedImage cover = new BufferedImage(coverWidth, coverHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D coverG2 = (Graphics2D) cover.getGraphics();
        coverG2.setColor(Color.BLACK);
        coverG2.fillRect(0, 0, coverWidth, coverHeight);
        coverG2.dispose();

        //开启透明度
        bgG2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
        //描绘
        bgG2.drawImage(cover, coverX, coverY, coverWidth, coverHeight, null);
        //结束透明度
        bgG2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        bgG2.dispose();

    }

    @SneakyThrows
    private String imageCoverUpload(String appId, String thumbnail) {
        File file = File.createTempFile(UUID.randomUUID().toString(), ".png");
        URL url = new URL(thumbnail);
        BufferedImage read = ImageIO.read(url);
        coverImg(read);
        playCoverImg(read);
        ImageIO.write(read, "png", file);
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


    @SneakyThrows
    private String imageUpload(String appId, String thumbnail) {
        File file = File.createTempFile(UUID.randomUUID().toString(), ".png");
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
