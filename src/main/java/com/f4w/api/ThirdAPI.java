package com.f4w.api;

import com.f4w.entity.juhe.DictionaryResult;
import com.f4w.entity.juhe.ZDResult;
import com.f4w.utils.HttpUtils;
import com.f4w.utils.R;
import org.apache.commons.collections4.ListUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/third")
public class ThirdAPI {

    @GetMapping("/xhzd/query")
    public R getArticleList(String word, String type,String riddle) throws IOException {
        Call<List<DictionaryResult>> result = HttpUtils.DictionaryService().dictionary(type, word,riddle);
        List<DictionaryResult> render = result.execute().body();
        if (render.isEmpty()) {
            return R.renderError("无查询结果");
        }
        return R.renderSuccess("data", render);
    }
}
