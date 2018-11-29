package com.f4w.service;

import com.f4w.entity.news.NewsResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface NewsService {
    @GET("/v2/top-headlines?apiKey=b9da6f6472c3412481cd12fb2d17e9d5")
    Call<NewsResult> topHeadlines(
            @Query("page") Integer page
            , @Query("pageSize") Integer pageSize
            , @Query("country") String country
            , @Query("category") String category
            , @Query("sources") String sources
            , @Query("q") String q

    );

    @GET("/v2/everything?apiKey=b9da6f6472c3412481cd12fb2d17e9d5")
    Call<NewsResult> everything(
            @Query("page") Integer page
            , @Query("pageSize") Integer pageSize
            , @Query("q") String q
            , @Query("language") String language
            , @Query("sources") String sources
            , @Query("excludeDomains") String excludeDomains
            , @Query("from") String from
            , @Query("to") String to
            , @Query("sortBy") String sortBy

    );

    @GET("/v2/sources?apiKey=b9da6f6472c3412481cd12fb2d17e9d5")
    Call<NewsResult> sources(
            @Query("category") String category
            , @Query("language") String language
            , @Query("country") String country
    );
}
