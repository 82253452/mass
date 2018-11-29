package com.f4w.entity.juhe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ZDContent {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("zi")
    @Expose
    private String zi;
    @SerializedName("py")
    @Expose
    private String py;
    @SerializedName("wubi")
    @Expose
    private String wubi;
    @SerializedName("pinyin")
    @Expose
    private String pinyin;
    @SerializedName("bushou")
    @Expose
    private String bushou;
    @SerializedName("bihua")
    @Expose
    private String bihua;
    @SerializedName("jijie")
    @Expose
    private List<String> jijie = null;
    @SerializedName("xiangjie")
    @Expose
    private List<String> xiangjie = null;
}
