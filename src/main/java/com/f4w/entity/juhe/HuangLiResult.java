package com.f4w.entity.juhe;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class HuangLiResult {
    private String reason;
    @SerializedName("result")
    HuangLiContent ResultObject;
    private Integer error_code;
}
