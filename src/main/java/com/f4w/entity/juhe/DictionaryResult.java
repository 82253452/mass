package com.f4w.entity.juhe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class DictionaryResult {
    @Expose
    @SerializedName("derivation")
    private String derivation;
    @Expose
    @SerializedName("example")
    private String example;
    @Expose
    @SerializedName("explanation")
    private String explanation;
    @Expose
    @SerializedName("pinyin")
    private String pinyin;
    @Expose
    @SerializedName("word")
    private String word;
    @Expose
    @SerializedName("abbreviation")
    private String abbreviation;
    @Expose
    @SerializedName("oldword")
    private String oldword;
    @Expose
    @SerializedName("strokes")
    private String strokes;
    @Expose
    @SerializedName("radicals")
    private String radicals;
    @Expose
    @SerializedName("more")
    private String more;
    @Expose
    @SerializedName("riddle")
    private String riddle;
    @Expose
    @SerializedName("answer")
    private String answer;
}
