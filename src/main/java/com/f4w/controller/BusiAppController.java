package com.f4w.controller;


import cn.binarywang.wx.miniapp.util.json.WxMaGsonBuilder;
import com.alibaba.fastjson.JSON;
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
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.ma.WxMaOpenCommitExtInfo;
import me.chanjar.weixin.open.bean.ma.WxOpenMaSubmitAudit;
import me.chanjar.weixin.open.bean.message.WxOpenMaSubmitAuditMessage;
import me.chanjar.weixin.open.bean.result.WxOpenMaCategoryListResult;
import me.chanjar.weixin.open.bean.result.WxOpenMaPageListResult;
import me.chanjar.weixin.open.bean.result.WxOpenMaSubmitAuditResult;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import me.chanjar.weixin.open.util.json.WxOpenGsonBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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

    private static final String ROOT_PATH = BusiAppController.class.getResource("/").getPath();

    /**
     * 发布已经审核通过的小程序
     *
     * @param appId
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/releaseWeapp")
    public R releaseWeapp(String appId) throws WxErrorException {
        WxOpenResult wxOpenResult = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).releaesAudited();
        if (!"0".equals(wxOpenResult.getErrcode())) {
            return R.error(wxOpenResult.getErrmsg());
        }
        //todo 更新状态
        return R.ok();
    }



    @GetMapping("/submitWeapp")
    public R submitWeapp(@RequestParam Map<String, String> param) throws WxErrorException {
        WxOpenMaPageListResult pagePath = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(param.get("appId")).getPageList();
        if (null == pagePath) {
            return R.error("pagePath 获取失败");
        }
        WxOpenMaSubmitAuditMessage wxOpenMaSubmitAuditMessage = new WxOpenMaSubmitAuditMessage();
        List<WxOpenMaSubmitAudit> itemList = new ArrayList<>();
        WxOpenMaSubmitAudit wxOpenMaSubmitAudit = new WxOpenMaSubmitAudit();
        wxOpenMaSubmitAudit.setPagePath(pagePath.toString());
        wxOpenMaSubmitAudit.setTag(param.get("tag"));
        wxOpenMaSubmitAudit.setTitle(param.get("title"));
        wxOpenMaSubmitAudit.setFirstClass(param.get("firstClass"));
        wxOpenMaSubmitAudit.setFirstId(Integer.valueOf(param.get("firstId")));
        wxOpenMaSubmitAudit.setSecondClass(param.get("secondClass"));
        wxOpenMaSubmitAudit.setSecondId(Integer.valueOf(param.get("secondId")));
        wxOpenMaSubmitAudit.setThirdClass(param.get("thirdClass"));
        wxOpenMaSubmitAudit.setThirdId(Integer.valueOf(param.get("thirdId")));
        itemList.add(wxOpenMaSubmitAudit);
        wxOpenMaSubmitAuditMessage.setItemList(itemList);
        WxOpenMaSubmitAuditResult resp = submitAudit(param.get("appId"), wxOpenMaSubmitAuditMessage);
        if (!"0".equals(resp.getErrcode())) {
            return R.error(resp.getErrmsg());
        }
        BusiApp busiApp = busiAppMapper.selectByPrimaryKey(param.get("id"));
        busiApp.setAuditId(resp.getAuditId());
        return R.ok();
    }

    @GetMapping("/getItemList")
    public R getItemList(String appId) throws WxErrorException {
        WxOpenMaCategoryListResult wxOpenMaCategoryListResult = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(appId).getCategoryList();
        return R.ok().put("list", wxOpenMaCategoryListResult.getCategoryList());
    }

    @GetMapping("/pushWeapp")
    public R pushWeapp(@RequestParam Map<String, String> param) throws WxErrorException {
        WxMaOpenCommitExtInfo extInfo = WxMaOpenCommitExtInfo.INSTANCE();
        extInfo.setExtAppid(param.get("appId"));
        WxOpenResult responseContent = codeCommit(param.get("appId"), 0L, "1.0.1", "第一次提交", extInfo);
        if (responseContent == null || !"0".equals(responseContent.getErrcode())) {
            return R.error();
        }
        return submitWeapp(param);
    }

    @GetMapping("/getTestQrcode")
    public byte[] getTestQrcode(String appId) {
        try {
            File file = wxOpenService
                    .getWxOpenComponentService()
                    .getWxMaServiceByAppid(appId)
                    .getTestQrcode("", new HashMap<>());
            return FileUtils.readFileToByteArray(file);
        } catch (IOException | WxErrorException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/getAuthUrl")
    public R getAuthUrl() throws WxErrorException {
        String url = wxOpenService.getWxOpenComponentService().getPreAuthUrl("https://dev.innter.fast4ward.cn/testApi/notify/authorizerRefreshToken", "2", "");
        return R.ok().put("url", url);
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
    public PageInfo<BusiApp> selectByPage(@RequestParam Map map) {
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        PageInfo<BusiApp> page = new PageInfo<>(busiAppMapper.selectAll());
        return page;
    }

    @GetMapping("/getAppPages")
    public Map getAppPages(@CurrentUser SysUser sysUser, @RequestParam Map map) {
        BusiAppPage busiAppPage = new BusiAppPage();
        busiAppPage.setUid(sysUser.getId());
        List<BusiAppPage> list = busiAppPageMapper.select(busiAppPage);
        Map render = new HashMap(10);
        list.forEach(e -> {
            render.put(e.getId(), e.getPageName());
        });
        return render;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody BusiApp BusiApp) {
        return busiAppMapper.insert(BusiApp);
    }

    @GetMapping("/selectById")
    public BusiApp selectById(String id) {
        return busiAppMapper.selectByPrimaryKey(id);
    }

    @PostMapping("/updateById")
    public int updateById(@RequestBody BusiApp BusiApp) {
        return busiAppMapper.updateByPrimaryKeySelective(BusiApp);
    }

    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody String ids) {
        return busiAppMapper.deleteByIds(ids);
    }

    @PostMapping("/deleteById")
    public int deleteById(@RequestBody Map param) {
        return busiAppMapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
    }

    private WxOpenResult codeCommit(String appId, Long templateId, String userVersion, String userDesc, WxMaOpenCommitExtInfo extInfo) throws WxErrorException {
        JsonObject params = new JsonObject();
        params.addProperty("template_id", templateId);
        params.addProperty("user_version", userVersion);
        params.addProperty("user_desc", userDesc);
        params.addProperty("ext_json", JSON.toJSONString(extInfo));
        String responseContent = wxOpenService
                .getWxOpenComponentService()
                .getWxMaServiceByAppid(appId).post("https://api.weixin.qq.com/wxa/commit", WxOpenGsonBuilder.create().toJson(params));
        return WxMaGsonBuilder.create().fromJson(responseContent, WxOpenResult.class);
    }

    private WxOpenMaSubmitAuditResult submitAudit(String appId, WxOpenMaSubmitAuditMessage submitAuditMessage) throws WxErrorException {
        String responseContent = wxOpenService
                .getWxOpenComponentService()
                .getWxMaServiceByAppid(appId).post("https://api.weixin.qq.com/wxa/submit_audit", WxOpenGsonBuilder.create().toJson(submitAuditMessage));
        return WxMaGsonBuilder.create().fromJson(responseContent, WxOpenMaSubmitAuditResult.class);
    }


    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("D:\\workTest\\comff42\\target\\classes\\weapp\\tmp\\5c7e48c3c3fc4f94849f33864e24b2b9\\personCard\\go.bat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





