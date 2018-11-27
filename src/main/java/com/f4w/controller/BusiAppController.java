package com.f4w.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.f4w.annotation.CurrentUser;
import com.f4w.annotation.TokenIntecerpt;
import com.f4w.entity.BusiApp;
import com.f4w.entity.BusiAppPage;
import com.f4w.entity.SysUser;
import com.f4w.freemarker.GeneratorZipFile;
import com.f4w.mapper.BusiAppMapper;
import com.f4w.mapper.BusiAppPageMapper;
import com.f4w.utils.R;
import com.f4w.weapp.WxOpenService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.ma.WxMaOpenCommitExtInfo;
import me.chanjar.weixin.open.bean.ma.WxMaOpenWindow;
import me.chanjar.weixin.open.bean.ma.WxOpenMaSubmitAudit;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String ROOT_PATH = BusiAppController.class.getResource("/").getPath();

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
        return R.ok().put("reason", resp.getString("reason"));
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
        String wxOpenResult = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).getLatestAuditStatus(null);
        JSONObject resp = JSONObject.parseObject(wxOpenResult);
        if (!"0".equals(resp.getString("errcode"))) {
            return R.error(resp.getString("errmsg"));
        }
        return R.ok().put("reason", resp.getString("reason"));
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
        BusiApp busiApp = busiAppMapper.selectByPrimaryKey(param.get("id"));
        busiApp.setAuditId(resp.getAuditId());
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
        return R.ok().put("list", wxOpenMaCategoryListResult.getCategoryList());
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
    @GetMapping("/pushWeapp")
    public R pushWeapp(@RequestParam Map<String, String> param) throws WxErrorException {
        BusiApp busiApp = new BusiApp();
        busiApp.setAppId(param.get("appId"));
        busiApp = busiAppMapper.selectOne(busiApp);
        if (null == busiApp) {
            return R.error("appId异常");
        }
        busiApp.setVersion(busiApp.getVersion() + 1);
        busiAppMapper.updateByPrimaryKeySelective(busiApp);
        WxMaOpenCommitExtInfo extInfo = WxMaOpenCommitExtInfo.INSTANCE();
        extInfo.setExtAppid(param.get("appId"));
        WxMaOpenWindow wxMaOpenWindow = new WxMaOpenWindow();
        wxMaOpenWindow.setBackgroundTextStyle("light");
        wxMaOpenWindow.setNavigationBarTitleText(busiApp.getNickName());
        wxMaOpenWindow.setNavigationBarTextStyle("black");
        extInfo.setWindow(wxMaOpenWindow);
        //set ext
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", "haha");
        jsonArray.add(jsonObject);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject.put("title", "haha");
        jsonArray.add(jsonObject2);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject.put("title", "haha");
        jsonArray.add(jsonObject3);
        JSONObject jsonObject4 = new JSONObject();
        jsonObject.put("title", "haha");
        jsonArray.add(jsonObject4);
        Map ext = new HashMap();
        ext.put("appId", param.get("appId"));
        ext.put("pages", jsonArray.toString());
        extInfo.setExtMap(ext);
        String responseContent = wxOpenService
                .getWxOpenComponentService()
                .getWxMaServiceByAppid(param.get("appId"))
                .codeCommit(Long.valueOf(stringRedisTemplate.opsForValue().get("templateId")), busiApp.getVersion().toString(), "提交审核", extInfo);
        JSONObject resp = JSONObject.parseObject(responseContent);
        if (!"0".equals(resp.getString("errcode"))) {
            return R.error(resp.getString("errmsg"));
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
    }
}





