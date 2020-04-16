package com.f4w.utils;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    private int pageNum;
    private int pageSize;
}
