package com.f4w.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: yp
 * @Date: 2020/10/13 19:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String address;
    private Double latitude;
    private Double longitude;
    private String name;
}
