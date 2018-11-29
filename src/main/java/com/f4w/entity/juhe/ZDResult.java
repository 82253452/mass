package com.f4w.entity.juhe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ZDResult {
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("result")
    @Expose
    private ZDContent result;
    @SerializedName("error_code")
    @Expose
    private Integer errorCode;
}
