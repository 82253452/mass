package com.f4w.service;

import com.f4w.entity.juhe.DictionaryResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface DictionaryService {
    @GET("/dictionary")
    Call<List<DictionaryResult>> dictionary(@Query("type") String type, @Query("word") String word,@Query("riddle")String riddle);
}
