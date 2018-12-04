package com.f4w.service;


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
}
