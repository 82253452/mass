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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
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
    private String fileSave;
    private String fileTemp;

    public GeneratorZipFile(BusiAppMapper busiAppMapper, BusiAppPageMapper busiAppPageMapper, Long id, String fileSave, String fileTemp) {
        this.busiAppPageMapper = busiAppPageMapper;
        this.busiAppMapper = busiAppMapper;
        this.id = id;
        this.fileSave = fileSave;
        this.fileTemp = fileTemp;
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
             e.printStackTrace();
             log.error("异常"+e.getMessage());
            initError();
        } finally {
            log.info("清理缓存");
            try {
                FileUtils.deleteDirectory(new File(fileTemp));
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
                        FileUtils.deleteQuietly(new File(fileSave + busiApp.getFileName() + ".zip"));
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
        File distPath = getTmpFile(tmpName, "personCard", "dist");
        File packPath = getPackageFile(tmpName + ".zip");
        ZipUtil.pack(distPath, packPath);
    }

    private void unPackZip() {
        log.info("解压缩文件");
        tmpName = UUID.randomUUID().toString().replaceAll("-", "");
        ZipUtil.unpack(getResourceFile("weapp", "app", "personCard.zip"), getTmpFile(tmpName));
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
                getTmpFile(tmpName, "personCard", "src", "app.js"),
                map);
        //index 模板处理
        for (int i = 0; i < PageList.size(); i++) {
            File indexTplToPath = getTmpFile(tmpName, "personCard", "src", "pages", "generator", "index_" + i + ".js");
            map.put("pageData", PageList.get(i));
            map.put("pageIndex", i);
            FreeMarkerTemplateUtils.generateFileByTemplate(tplPath + "index.ftl",
                    indexTplToPath,
                    map);
        }
        //project json 模板处理
        BusiApp busiApp = busiAppMapper.selectByPrimaryKey(id);
        if (null != busiApp) {
            map.put("appId", busiApp.getAppId());
        }
        FreeMarkerTemplateUtils.generateFileByTemplate(tplPath + "projectConfig.ftl",
                getTmpFile(tmpName, "personCard", "project.config.json"),
                map);
    }

    private void commondShell() throws IOException, InterruptedException {
        log.info("执行shell");
        System.out.println("执行build");
        String tmpPath = getTmpPath(tmpName, "personCard");
        String[] command = new String[]{"cd " + tmpPath, "npm run build:weapp"};
        System.out.println(command[0]);
        System.out.println(command[1]);
        Process process = Runtime.getRuntime().exec(command);
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

    private File getTmpFile(String... path) {
        String rPath = fileTemp;
        for (int i = 0; i < path.length; i++) {
            rPath += path[i] + File.separator;
        }
        File file = new File(rPath.substring(0, rPath.length() - 1));
        System.out.println("temp File" + file);
        return file;
    }

    private String getTmpPath(String... path) {
        String rPath = fileTemp;
        for (int i = 0; i < path.length; i++) {
            rPath += path[i] + File.separator;
        }
        return rPath;
    }

    private File getPackageFile(String... path) {
        String rPath = fileSave;
        for (int i = 0; i < path.length; i++) {
            rPath += path[i] + File.separator;
        }
        File file = new File(rPath.substring(0, rPath.length() - 1));
        return file;
    }

//    private File getResourceFile(String... path) {
//        String rPath = "";
//        for (int i = 0; i < path.length; i++) {
//            rPath += path[i] + File.separator;
//        }
//        File file = null;
//        try {
//            System.out.println("resourceFile path " + rPath.substring(0, rPath.length() - 1));
//            file = ResourceUtils.getFile("classpath:" + rPath.substring(0, rPath.length() - 1));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        System.out.println("resourceFile" + file);
//        return file;
//    }

    private InputStream getResourceFile(String... path) {
        String rPath = "";
        for (int i = 0; i < path.length; i++) {
            rPath += path[i] + File.separator;
        }
        InputStream stream = getClass().getClassLoader().getResourceAsStream(rPath.substring(0, rPath.length() - 1));
        return stream;
    }


}
