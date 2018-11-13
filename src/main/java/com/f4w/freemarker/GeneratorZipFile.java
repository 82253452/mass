package com.f4w.freemarker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.f4w.controller.BusiAppController;
import com.f4w.entity.BusiApp;
import com.f4w.entity.BusiAppPage;
import com.f4w.mapper.BusiAppMapper;
import com.f4w.mapper.BusiAppPageMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class GeneratorZipFile implements Runnable {
    //文件保存路径
    private String tmpName = "";
    private JSONArray jsonArray;
    private BusiAppMapper busiAppMapper;
    private BusiAppPageMapper busiAppPageMapper;
    private Long id;
    private String tmpFilePath;

    public GeneratorZipFile(BusiAppMapper busiAppMapper, BusiAppPageMapper busiAppPageMapper, Long id, String tmpFilePath) {
        this.busiAppPageMapper = busiAppPageMapper;
        this.busiAppMapper = busiAppMapper;
        this.id = id;
        this.tmpFilePath = tmpFilePath;
    }

    private static final String ROOT_PATH = BusiAppController.class.getResource("/").getPath();

    @Override
    public void run() {
        try {
            //初始化
            initEntity();
            //解压缩文件
            unPackZip();
            //处理模板
            freeMarker();
            //执行shell
            commondShell();
            //压缩文件
            zipFile();
            //更新表状态
            updateEntity();
        } catch (Exception e) {
            log.error("异常");
            initError();
            e.printStackTrace();
        } finally {
            log.info("清理缓存");
            try {
                FileUtils.deleteDirectory(new File(getFilePath("weapp", "tmp", tmpName)));
            } catch (IOException e) {
                log.error("清理缓存异常");
            }
        }

    }

    private void initError() {
        BusiApp busiApp = busiAppMapper.selectByPrimaryKey(id);
        if (null != busiApp) {
            busiApp.setFileName("");
            busiApp.setStatus(3);
            busiAppMapper.updateByPrimaryKeySelective(busiApp);
        } else {
            log.error("id不存在");
        }
    }

    private void initEntity() {
        BusiApp busiApp = busiAppMapper.selectByPrimaryKey(id);
        if (null != busiApp) {
            if (null != busiApp.getPageId()) {
                BusiAppPage busiAppPage = busiAppPageMapper.selectByPrimaryKey(busiApp.getPageId());
                if (null != busiAppPage) {
                    jsonArray = JSON.parseArray(busiAppPage.getContent());
                    if (StringUtils.isNotBlank(busiApp.getFileName())) {
                        FileUtils.deleteQuietly(new File(getFilePath("weapp", "downloadFile", busiApp.getFileName() + ".zip")));
                    }
                    busiApp.setFileName("");
                    busiApp.setStatus(2);
                    busiAppMapper.updateByPrimaryKeySelective(busiApp);
                }
            }
        } else {
            log.error("id不存在");
        }
    }

    private void updateEntity() {
        BusiApp busiApp = busiAppMapper.selectByPrimaryKey(id);
        if (null != busiApp) {
            busiApp.setStatus(1);
            busiApp.setFileName(tmpName);
            busiAppMapper.updateByPrimaryKeySelective(busiApp);
        } else {
            log.error("id不存在");
        }
    }

    private void zipFile() {
        log.info("压缩文件");
        String distPath = getPath("weapp", "tmp", tmpName, "personCard", "dist");
//        String packPath = getPath("weapp", "downloadFile", tmpName + ".zip");
        String packPath = tmpFilePath + tmpName + ".zip";
        ZipUtil.pack(new File(distPath), new File(packPath));
    }

    private void unPackZip() {
        log.info("解压缩文件");
        tmpName = UUID.randomUUID().toString().replaceAll("-", "");
        ZipUtil.unpack(new File(getFilePath("weapp", "app", "personCard.zip")), new File(getPath("weapp", "tmp", tmpName)));
    }

    private void freeMarker() throws Exception {
        log.info("模板文件处理");
        String tplPath = "weapp" + File.separator + "tpl" + File.separator;
        //数据
        List<PageEntity> PageList = jsonArray.toJavaList(PageEntity.class);
        Map map = new HashMap();
        map.put("PageList", PageList);
        //app模板处理
        FreeMarkerTemplateUtils.generateFileByTemplate(tplPath + "app.ftl",
                new File(getFilePath("weapp", "tmp", tmpName, "personCard", "src", "app.js")),
                map);
        //index 模板处理
        for (int i = 0; i < PageList.size(); i++) {
            String indexTplToPath = getFilePath("weapp", "tmp", tmpName, "personCard", "src", "pages", "generator", "index_" + i + ".js");
            map.put("pageData", PageList.get(i));
            map.put("pageIndex", i);
            FreeMarkerTemplateUtils.generateFileByTemplate(tplPath + "index.ftl",
                    new File(indexTplToPath),
                    map);
        }
        //project json 模板处理
        BusiApp busiApp = busiAppMapper.selectByPrimaryKey(id);
        if (null != busiApp) {
            map.put("appId", busiApp.getAppId());
        }
        FreeMarkerTemplateUtils.generateFileByTemplate(tplPath + "projectConfig.ftl",
                new File(getFilePath("weapp", "tmp", tmpName, "personCard", "project.config.json")),
                map);
    }

    private void commondShell() throws IOException, InterruptedException {
        log.info("执行shell");
        System.out.println("执行build");
        String shellPath = getPath("weapp", "shell");
        String tmpPath = getPath("weapp", "tmp", tmpName, "personCard");
        if ("/".equals(tmpPath.substring(0, 1))) {
            tmpPath = tmpPath.substring(1);
        }
        if ("/".equals(shellPath.substring(0, 1))) {
            shellPath = shellPath.substring(1);
        }
        Process process = Runtime.getRuntime().exec(shellPath + "go.bat " + tmpPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        log.info(sb.toString());
        process.waitFor();
    }

    private String getPath(String... path) {
        String rPath = ROOT_PATH;
        for (int i = 0; i < path.length; i++) {
            rPath += path[i] + File.separator;
        }
        return rPath;
    }

    private String getFilePath(String... path) {
        String rPath = ROOT_PATH;
        for (int i = 0; i < path.length; i++) {
            rPath += path[i] + File.separator;
        }
        return rPath.substring(0, rPath.length() - 1);
    }
}
