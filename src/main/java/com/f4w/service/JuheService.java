package com.f4w.service;


import com.alibaba.fastjson.JSONObject;
import com.f4w.entity.juhe.HuangLiResult;
import com.f4w.entity.juhe.ZDResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JuheService {
    @GET("xhzd/query?key=140a2cb69dcff1ed60c11287cae34b9c")
    Call<ZDResult> getXHZDQeury(@Query("word") String word);
    @GET("laohuangli/d?key=5c00b0156131250907af04b2d646cbd3")
    Call<HuangLiResult> laohuangli(@Query("date") String date);
    @GET("cook/querykey=b02a598f948eb0595365c302c14d1f65")
    Call<JSONObject> cookQuery(@Query("pn") String pn, @Query("rn") String rn, @Query("menu") String menu);
    @GET("cook/category?key=b02a598f948eb0595365c302c14d1f65")
    Call<JSONObject> cookCategory();
    @GET("cook/index?key=b02a598f948eb0595365c302c14d1f65")
    Call<JSONObject> cookIndex(@Query("pn") String pn, @Query("rn") String rn, @Query("cid") String cid);
    @GET("cook/queryid?key=b02a598f948eb0595365c302c14d1f65")
    Call<JSONObject> cookQueryid(@Query("id") String id);
}
