package com.neuedu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoVo {
    String sortBy;
    String keyWords;
    int pageNum;
    int pageSize;
    String status;
}
