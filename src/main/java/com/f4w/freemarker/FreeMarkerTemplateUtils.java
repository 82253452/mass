package com.f4w.freemarker;

import com.alibaba.fastjson.JSONArray;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.util.Map;

/**
 * Created by Ay on 2016/7/27.
 */
public class FreeMarkerTemplateUtils {

    private FreeMarkerTemplateUtils() {
    }

    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_22);

    static {
        //这里比较重要，用来指定加载模板所在的路径
//        try {
//            CONFIGURATION.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTemplateUtils.class,""));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        CONFIGURATION.setClassForTemplateLoading(FreeMarkerTemplateUtils.class,"/");
        CONFIGURATION.setDefaultEncoding("UTF-8");
        CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CONFIGURATION.setCacheStorage(NullCacheStorage.INSTANCE);
    }

    public static Template getTemplate(String templateName) throws IOException {
        try {
            return CONFIGURATION.getTemplate(templateName);
        } catch (IOException e) {
            throw e;
        }
    }

    public static void generateFileByTemplate(final String templateName, File file, Map<String, Object> dataMap) throws Exception {
        Template template = getTemplate(templateName);
        FileOutputStream fos = new FileOutputStream(file);
        Writer out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 10240);
        template.process(dataMap, out);
    }

    public static void clearCache() {
        CONFIGURATION.clearTemplateCache();
    }

}
