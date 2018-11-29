package com.f4w.service;


import com.f4w.entity.juhe.ZDResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JuheService {
    @GET("xhzd/query?key=140a2cb69dcff1ed60c11287cae34b9c")
    Call<ZDResult> getXHZDQeury(@Query("word") String word);
}
