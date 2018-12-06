package com.f4w.api;

import com.alibaba.fastjson.JSONObject;
import com.f4w.entity.juhe.DictionaryResult;
import com.f4w.entity.news.NewsResult;
import com.f4w.utils.HttpUtils;
import com.f4w.utils.R;
import com.github.kevinsawicki.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Call;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/third")
public class ThirdAPI {

    @GetMapping("/xhzd/query")
    public R getArticleList(String word, String type, String riddle) throws IOException {
        Call<List<DictionaryResult>> result = HttpUtils.DictionaryService().dictionary(type, word, riddle);
        List<DictionaryResult> render = result.execute().body();
        if (render.isEmpty()) {
            return R.renderError("无查询结果");
        }
        return R.renderSuccess("data", render);
    }

    @GetMapping("/news/topHeadlines")
    public R topHeadlines(Integer page, Integer pageSize, String country, String sources, String category, String q) throws IOException {
        Call<NewsResult> result = HttpUtils.NewsService().topHeadlines(page, pageSize, country, category, sources, q);
        NewsResult render = result.execute().body();
        if (null == render) {
            return R.renderError("无查询结果");
        }
        return R.renderSuccess("data", render);
    }

    @GetMapping("/news/everything")
    public R everything(Integer page, Integer pageSize, String language, String q) throws IOException {
        Call<NewsResult> result = HttpUtils.NewsService().everything(1, 10, q, language, null, null, null, null, null);
        NewsResult render = result.execute().body();
        if (null == render) {
            return R.renderError("无查询结果");
        }
        return R.renderSuccess("data", render);
    }

    @GetMapping("/nameRate")
    public R laohuangli(String fName, String name)  {
        JSONObject s = nameRate(fName, name);
        return R.renderSuccess("data", s);
    }

    @GetMapping("/nameMathing")
    public R nameMath(String fName, String name)  {
        JSONObject s = nameMathing(fName, name);
        return R.renderSuccess("data", s);
    }

    private JSONObject nameMathing(String fName, String sName) {
        Map<String, String> data = new HashMap<>();
        data.put("xingx", fName);
        data.put("mingx", sName);
        HttpRequest request = HttpRequest.post("http://www.sheup.com/xingmingpeidui.php");
        String s = request.form(data, "gbk").body("gbk");
        Document doc = Jsoup.parse(s);
        Elements cList = doc.select(".crights .c_1");
        String num = cList.get(1).select(".c1_text p").get(1).select("font").text();
        String title = cList.get(1).select(".c1_text p").get(2).select("strong").text();
        String text = cList.get(1).select(".c1_text p").get(3).text();
        String qianshi = cList.get(1).select(".c1_text p").get(4).text();
        String qianshiText = cList.get(1).select(".c1_text p").get(5).text();
        JSONObject result = new JSONObject();
        result.put("num", num);
        result.put("title", title);
        result.put("text", text);
        result.put("qianshi", qianshi);
        result.put("qianshiText", qianshiText);
        System.out.println(result.toJSONString());
        return result;
    }
    private JSONObject nameRate(String fName, String sName) {
        Map<String, String> data = new HashMap<>();
        data.put("xingx", fName);
        data.put("mingx", sName);
        HttpRequest request = HttpRequest.post("http://www.sheup.com/xingming_dafen.php");
        String s = request.form(data, "gbk").body("gbk");
        Document doc = Jsoup.parse(s);
        String dafen = doc.select(".crights .c_dafen .dafen_right .dr_ass span").text();
        Elements cList = doc.select(".crights .c_1");
        Elements pList = cList.get(1).select("p");
        JSONObject result = new JSONObject();
        //打分
        result.put("dafen", dafen);
        //姓名分析
        Elements pFir = cList.get(2).select(".c1_text");
        Elements nameTr = pFir.select("table tr");
        Elements firstName = nameTr.get(1).select("td");
        result.put("firstName", firstName.get(0).text());
        result.put("firstNameFan", firstName.get(1).text());
        result.put("firstNamePin", firstName.get(2).text());
        result.put("firstNameWuxing", firstName.get(3).text());
        result.put("firstNameBihua", firstName.get(4).text());
        result.put("firstNameJieShi", firstName.get(5).text());
        Elements name = nameTr.get(2).select("td");
        result.put("name", name.get(0).text());
        result.put("nameFan", name.get(1).text());
        result.put("namePin", name.get(2).text());
        result.put("nameWuxing", name.get(3).text());
        result.put("nameBihua", name.get(4).text());
        result.put("nameJieShi", name.get(5).text());
        //命格
        Elements minge = pFir.select("p strong");
        result.put("tiange", minge.get(0).text());
        result.put("renge", minge.get(1).text());
        result.put("dige", minge.get(2).text());
        result.put("waige", minge.get(3).text());
        result.put("zonge", minge.get(4).text());
        //详解
        final String[] xiangjie = {""};
        AtomicInteger i = new AtomicInteger();
        pList.forEach(e -> {
            if (i.get() == pList.size() - 1) {
                return;
            }
            xiangjie[0] += e.text() + "\n";
            i.getAndIncrement();
        });
        result.put("xiangjie", xiangjie[0]);
        return result;
    }


}
