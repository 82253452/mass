package com.f4w.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.f4w.annotation.CurrentUser;
import com.f4w.annotation.TokenIntecerpt;
import com.f4w.entity.BusiApp;
import com.f4w.entity.BusiAppPage;
import com.f4w.entity.SysUser;
import com.f4w.entity.Wxmp;
import com.f4w.freemarker.GeneratorZipFile;
import com.f4w.mapper.BusiAppMapper;
import com.f4w.mapper.BusiAppPageMapper;
import com.f4w.mapper.WxmpMapper;
import com.f4w.utils.R;
import com.f4w.weapp.WxOpenService;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.open.bean.WxOpenMaCodeTemplate;
import me.chanjar.weixin.open.bean.ma.*;
import me.chanjar.weixin.open.bean.message.WxOpenMaSubmitAuditMessage;
import me.chanjar.weixin.open.bean.result.WxOpenMaCategoryListResult;
import me.chanjar.weixin.open.bean.result.WxOpenMaPageListResult;
import me.chanjar.weixin.open.bean.result.WxOpenMaSubmitAuditResult;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;

@Slf4j
@RestController
@RequestMapping("/busiApp")
@TokenIntecerpt
public class BusiAppController {
    @Resource
    private BusiAppMapper busiAppMapper;
    @Resource
    private BusiAppPageMapper busiAppPageMapper;
    @Resource
    private ExecutorService threadPoolExecutor;

    @Value("${path.filesave}")
    private String filesave;
    @Value("${path.filetemp}")
    private String filetemp;

    @Resource
    private WxOpenService wxOpenService;
    @Resource
    private WxmpMapper wxmpMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String ROOT_PATH = BusiAppController.class.getResource("/").getPath();

    /**
     * 定时发布文章
     *
     * @return
     */
    @GetMapping("/autoMessageApi")
    public R autoMessageApi(String appId, Integer type, Integer num) {
        BusiApp busiApp = new BusiApp();
        busiApp.setAppId(appId);
        busiApp = busiAppMapper.selectOne(busiApp);
        if (!busiApp.getAutoMessage().equals(1)) {
            busiApp.setAutoMessage(1);//自动推送
            busiApp.setMessageType(type);//类型
            busiAppMapper.updateByPrimaryKeySelective(busiApp);
        }
        Map param = new HashMap();
        param.put("executorHandler", "test");
        param.put("jobCron", "0 0 10 * * ?");
        param.put("executorParam", "111");
        param.put("jobGroup", "weArticle");
        param.put("jobDesc", "weArticle");
        param.put("executorRouteStrategy", "FIRST");
        param.put("glueType", "BEAN");
        param.put("executorBlockStrategy", "SERIAL_EXECUTION");
        param.put("author", "yp");
        String r = HttpRequest.post("https://zhihuizhan.net/xxl-job-admin/jobinfo/add").form(param).body();
        log.info(r);
        return R.renderSuccess(true);
    }

    /**
     * 获取当前模板
     *
     * @return
     */
    @GetMapping("/getCurrentTemplateId")
    public R getCurrentTemp() {
        String templateId = stringRedisTemplate.opsForValue().get("templateId");
        return R.renderSuccess("templateId", templateId);
    }

    /**
     * put当前模板
     *
     * @return
     */
    @GetMapping("/setCurrentTemplateId")
    public R setCurrentTemplateId(String templateId) {
        stringRedisTemplate.opsForValue().set("templateId", templateId);
        return R.ok();
    }

    /**
     * 获取草稿箱内的所有临时代码草稿
     *
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/gettemplatedraftlist")
    public R gettemplatedraftlist() throws WxErrorException {
        List<WxOpenMaCodeTemplate> templateDraftList = wxOpenService.getWxOpenComponentService().getTemplateDraftList();
        if (templateDraftList.isEmpty()) {
            return R.ok();
        }
        return R.renderSuccess("templateDraftList", templateDraftList);
    }

    /**
     * 获取代码模版库中的所有小程序代码模版
     *
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/gettemplatelist")
    public R gettemplatelist() throws WxErrorException {
        List<WxOpenMaCodeTemplate> templateList = wxOpenService.getWxOpenComponentService().getTemplateList();
        if (templateList.isEmpty()) {
            return R.ok();
        }
        return R.renderSuccess("templateList", templateList);
    }

    /**
     * 将草稿箱的草稿选为小程序代码模版
     *
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/addtotemplate")
    public R addtotemplate(Long draftId) throws WxErrorException {
        wxOpenService.getWxOpenComponentService().addToTemplate(draftId);
        return R.ok();
    }

    /**
     * 删除指定小程序代码模版
     *
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/deleteTemplate")
    public R deleteTemplate(Long templateId) throws WxErrorException {
        wxOpenService.getWxOpenComponentService().deleteTemplate(templateId);
        return R.ok();
    }


    /**
     * 开启自动答题回复
     *
     * @param appId
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/startReplay")
    public R startReplay(String appId) throws WxErrorException {

        return R.ok();
    }

    /**
     * 查询某个指定版本的审核状态
     *
     * @param appId
     * @return
     * @throws WxErrorException
     */
    @GetMapping("getAuditstatus")
    public R getAuditstatus(String appId, Long auditid) throws WxErrorException {
        BusiApp busiApp = new BusiApp();
        busiApp.setAppId(appId);
        busiApp = busiAppMapper.selectOne(busiApp);
        String wxOpenResult = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).getAuditStatus(busiApp.getAuditId());
        JSONObject resp = JSONObject.parseObject(wxOpenResult);
        if (!"0".equals(resp.getString("errcode"))) {
            return R.error(resp.getString("errmsg"));
        }
        return R.renderSuccess("reason", resp.getString("reason"));
    }

    /**
     * 查询某个指定版本的审核状态
     *
     * @param appId
     * @return
     * @throws WxErrorException
     */
    @GetMapping("getLastAuditstatus")
    public R getLastAuditstatus(String appId) throws WxErrorException {
        BusiApp busiApp = new BusiApp();
        busiApp.setAppId(appId);
        busiApp = busiAppMapper.selectOne(busiApp);

        String wxOpenResult = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).getLatestAuditStatus(null);
        JSONObject resp = JSONObject.parseObject(wxOpenResult);
        if (!"0".equals(resp.getString("errcode"))) {
            busiApp.setStatus(6);
            busiApp.setAuditMsg(resp.getString("errmsg"));
            return R.error(resp.getString("errmsg"));
        } else {
            busiApp.setStatus(5);
            busiApp.setAuditMsg("发布成功");
        }
        busiAppMapper.updateByPrimaryKey(busiApp);
        return R.renderSuccess("reason", resp.getString("reason"));
    }

    /**
     * 发布已经审核通过的小程序
     *
     * @param appId
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/releaseWeapp")
    public R releaseWeapp(String appId) throws WxErrorException {
        String wxOpenResult = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).releaesAudited();
        JSONObject resp = JSONObject.parseObject(wxOpenResult);
        if (!"0".equals(resp.getString("errcode"))) {
            return R.error(resp.getString("errmsg"));
        }
        //todo 更新状态
        return R.ok();
    }


    /**
     * 将第三方提交的代码包提交审核
     *
     * @param param
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/submitWeapp")
    public R submitWeapp(@RequestParam Map<String, String> param) throws WxErrorException {
        WxOpenMaPageListResult pagePath = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(param.get("appId")).getPageList();
        if (null == pagePath) {
            return R.error("pagePath 获取失败");
        }
        WxOpenMaSubmitAuditMessage wxOpenMaSubmitAuditMessage = new WxOpenMaSubmitAuditMessage();
        List<WxOpenMaSubmitAudit> itemList = new ArrayList<>();
        WxOpenMaSubmitAudit wxOpenMaSubmitAudit = new WxOpenMaSubmitAudit();
        wxOpenMaSubmitAudit.setPagePath(pagePath.getPageList().get(0));
        wxOpenMaSubmitAudit.setTag(param.get("tag"));
        wxOpenMaSubmitAudit.setTitle(param.get("title"));
        wxOpenMaSubmitAudit.setFirstClass(param.get("firstClass"));
        wxOpenMaSubmitAudit.setFirstId(Integer.valueOf(param.get("firstId")));
        wxOpenMaSubmitAudit.setSecondClass(param.get("secondClass"));
        wxOpenMaSubmitAudit.setSecondId(Integer.valueOf(param.get("secondId")));
        wxOpenMaSubmitAudit.setThirdClass(param.get("thirdClass") == null ? "" : param.get("thirdClass"));
        wxOpenMaSubmitAudit.setThirdId(Integer.valueOf(param.get("thirdId") == null ? "0" : param.get("thirdId")));
        itemList.add(wxOpenMaSubmitAudit);
        wxOpenMaSubmitAuditMessage.setItemList(itemList);
        WxOpenMaSubmitAuditResult resp = wxOpenService
                .getWxOpenComponentService()
                .getWxMaServiceByAppid(param.get("appId"))
                .submitAudit(wxOpenMaSubmitAuditMessage);
        if (!"0".equals(resp.getErrcode())) {
            return R.error(resp.getErrmsg());
        }
        BusiApp busiApp = new BusiApp();
        busiApp.setAppId(param.get("appId"));
        busiApp = busiAppMapper.selectOne(busiApp);
        busiApp.setAuditId(resp.getAuditId());
        busiApp.setStatus(2);
        busiAppMapper.updateByPrimaryKey(busiApp);
        return R.ok();
    }

    /**
     * 获取授权小程序帐号的可选类目
     *
     * @param appId
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/getItemList")
    public R getItemList(String appId) throws WxErrorException {
        WxOpenMaCategoryListResult wxOpenMaCategoryListResult = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).getCategoryList();
        return R.renderSuccess("list", wxOpenMaCategoryListResult.getCategoryList());
    }


    public List<String> getPageList(String appId) throws WxErrorException {
        WxOpenMaPageListResult wxOpenMaPageListResult = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).getPageList();
        if (!"0".equals(wxOpenMaPageListResult.getErrcode())) {
            return null;
        }
        return wxOpenMaPageListResult.getPageList();
    }

    /**
     * 为授权的小程序帐号上传小程序代码
     *
     * @param param
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/onlyPushWeapp")
    public R onlyPushWeapp(@RequestParam Map<String, String> param) throws WxErrorException {
        BusiApp busiApp = new BusiApp();
        busiApp.setAppId(param.get("appId"));
        busiApp = busiAppMapper.selectOne(busiApp);
        if (null == busiApp) {
            return R.error("appId异常");
        }
        if (null == param.get("pageId")) {
            return R.error("pageId 不能为空");
        }
        BusiAppPage busiAppPage = busiAppPageMapper.selectByPrimaryKey(param.get("pageId"));
        if (null == busiAppPage) {
            return R.error("pageId异常");
        }
        busiApp.setVersion(busiApp.getVersion() == null ? 0 : busiApp.getVersion() + 1);
        busiApp.setPageId(MapUtils.getLong(param, "pageId"));
        busiApp.setStatus(7);
        busiAppMapper.updateByPrimaryKeySelective(busiApp);
        WxMaOpenCommitExtInfo extInfo = WxMaOpenCommitExtInfo.INSTANCE();
        extInfo.setExtAppid(param.get("appId"));
        WxMaOpenWindow wxMaOpenWindow = new WxMaOpenWindow();
        wxMaOpenWindow.setBackgroundTextStyle("light");
        wxMaOpenWindow.setNavigationBarTitleText(busiApp.getNickName());
        wxMaOpenWindow.setNavigationBarTextStyle("black");
        if (busiAppPage.getCustom() == 1) {
            wxMaOpenWindow.setNavigationStyle("custom");
        }
        extInfo.setWindow(wxMaOpenWindow);
        String content = busiAppPage.getContent();
        if (StringUtils.isBlank(content)) {
            return R.error("配置页面异常");
        }
        JSONArray pageContent = JSON.parseArray(content);
        //set ext
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appId", param.get("appId"));
        jsonArray.add(jsonObject);
        Map ext = new HashMap();
        ext.put("appId", param.get("appId"));
        ext.put("pages", jsonArray.toString());
        extInfo.setExtMap(ext);

        if (pageContent.size() > 1) {
            WxMaOpenTabBar wxMaOpenTabBar = new WxMaOpenTabBar();
            List<WxMaOpenTab> tabList = new ArrayList<>();
            for (int i = 0; i < pageContent.size(); i++) {
                WxMaOpenTab wxMaOpenTab = new WxMaOpenTab("pages/index/index_" + i
                        , pageContent.getJSONObject(i).getString("pageName"));
                if (StringUtils.isNotBlank(pageContent.getJSONObject(i).getString("iconName"))) {
                    wxMaOpenTab.setIconPath("/image/" + pageContent.getJSONObject(i).getString("iconName") + ".png");
                    wxMaOpenTab.setSelectedIconPath("/image/" + pageContent.getJSONObject(i).getString("iconName") + "_fill.png");
                }
                tabList.add(wxMaOpenTab);
            }
            wxMaOpenTabBar.setTabList(tabList);
            extInfo.setTabBar(wxMaOpenTabBar);
        }
        List<String> pageList = new ArrayList<>();
        pageList.add("pages/index/index_0");
        pageList.add("pages/index/index_1");
        pageList.add("pages/index/index_2");
        pageList.add("pages/index/index_3");
        pageList.add("pages/login/login");
        pageList.add("pages/List/index");
        pageList.add("pages/center/index");
        pageList.add("pages/about/index");
        pageList.add("pages/detail/index");
        pageList.add("pages/NewsDetail/index");
        pageList.add("pages/cookDetail/index");
        pageList.add("pages/cookNews/index");
        pageList.add("pages/cookVideo/index");
        pageList.add("pages/cookMenu/index");
        pageList.add("pages/goodsHelloDetail/index");
        extInfo.setPageList(pageList);
        String responseContent = wxOpenService
                .getWxOpenComponentService()
                .getWxMaServiceByAppid(param.get("appId"))
                .codeCommit(Long.valueOf(stringRedisTemplate.opsForValue().get("templateId")), busiApp.getVersion().toString(), "提交审核", extInfo);
        JSONObject resp = JSONObject.parseObject(responseContent);
        if (!"0".equals(resp.getString("errcode"))) {
            return R.error(resp.getString("errmsg"));
        }
        return R.ok();
    }

    /**
     * 为授权的小程序帐号上传小程序代码
     *
     * @param param
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/pushWeapp")
    public R pushWeapp(@RequestParam Map<String, String> param) throws WxErrorException {
        R push = onlyPushWeapp(param);
        if (!push.isOk()) {
            return R.error();
        }
        return submitWeapp(param);
    }

    /**
     * 获取体验二维码
     *
     * @param appId
     * @return
     */
    @GetMapping("/getTestQrcode")
    public byte[] getTestQrcode(String appId) {
        try {
            File file = wxOpenService
                    .getWxOpenComponentService()
                    .getWxMaServiceByAppid(appId)
                    .getTestQrcode(getPageList(appId).get(0), new HashMap<>());
            return FileUtils.readFileToByteArray(file);
        } catch (IOException | WxErrorException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/getAuthUrl")
    public R getAuthUrl(@CurrentUser SysUser sysUser) throws WxErrorException {
        String url = wxOpenService.getWxOpenComponentService().getPreAuthUrl("https://zhihuizhan.net/web/notify/authorizerRefreshToken?uid=" + sysUser.getId());
        return R.renderSuccess("url", url);
    }

    @GetMapping("/generator")
    public R generator(String id) {
        BusiApp busiApp = busiAppMapper.selectByPrimaryKey(id);
        if (null != busiApp && null != busiApp.getPageId()) {
            busiApp.setStatus(2);
            busiAppMapper.updateByPrimaryKeySelective(busiApp);
            threadPoolExecutor.execute(new GeneratorZipFile(busiAppMapper, busiAppPageMapper, busiApp.getId(), filesave, filetemp));
            return R.ok();
        }
        return R.error();
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<byte[]> downloadFile(String id) throws IOException {
        BusiApp busiApp = busiAppMapper.selectByPrimaryKey(id);
        if (null != busiApp && StringUtils.isNotBlank(busiApp.getFileName())) {
//            String packPath = getFilePath("weapp", "downloadFile", busiApp.getFileName() + ".zip");
//            if ("/".equals(packPath.substring(0, 1))) {
//                packPath = packPath.substring(1);
//            }
            String packPath = filesave + busiApp.getFileName() + ".zip";
            HttpHeaders headers = new HttpHeaders();
            byte[] f = FileUtils.readFileToByteArray(new File(packPath));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", busiApp.getFileName() + ".zip");
            return new ResponseEntity<>(f,
                    headers, HttpStatus.CREATED);
        }
        return null;
    }

    private String getFilePath(String... path) {
        String rPath = ROOT_PATH;
        for (int i = 0; i < path.length; i++) {
            rPath += path[i] + File.separator;
        }
        return rPath.substring(0, rPath.length() - 1);
    }


    @GetMapping("/selectByPage")
    public R selectByPage(@CurrentUser SysUser user, @RequestParam Map map) {
        BusiApp busiApp = new BusiApp();
        busiApp.setUid(user.getId());
        busiApp.setMiniProgramInfo(MapUtils.getInteger(map, "miniType"));
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        PageInfo<BusiApp> page = new PageInfo<>(busiAppMapper.select(busiApp));
        return R.ok().put("data", page);
    }

    @GetMapping("/getAppPages")
    public R getAppPages(@CurrentUser SysUser sysUser, @RequestParam Map map) {
        BusiAppPage busiAppPage = new BusiAppPage();
        busiAppPage.setUid(sysUser.getId());
        List<BusiAppPage> list = busiAppPageMapper.select(busiAppPage);
        Map render = new HashMap(10);
        list.forEach(e -> {
            render.put(e.getId(), e.getPageName());
        });
        return R.ok().put("data", render);
    }

    @PostMapping("/insert")
    public R insert(@RequestBody BusiApp BusiApp) {
        int r = busiAppMapper.insert(BusiApp);
        return R.ok().put("data", r);
    }

    @GetMapping("/selectById")
    public R selectById(String id) {
        BusiApp r = busiAppMapper.selectByPrimaryKey(id);
        return R.ok().put("data", r);
    }

    @PostMapping("/updateById")
    public R updateById(@RequestBody BusiApp BusiApp) {
        int r = busiAppMapper.updateByPrimaryKeySelective(BusiApp);
        return R.ok().put("data", r);
    }

    @PostMapping("/deleteByIds")
    public R deleteByIds(@RequestBody String ids) {
        int r = busiAppMapper.deleteByIds(ids);
        return R.ok().put("data", r);
    }

    @PostMapping("/deleteById")
    public R deleteById(@RequestBody Map param) {
        int r = busiAppMapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
        return R.ok().put("data", r);
    }

    public static void main(String[] args) {
        HttpRequest request = HttpRequest.get("https://v.qq.com/x/cover/d6h5xo35kg0qswb.html");
        String s = request.body();
        System.out.println(s);
    }
}





