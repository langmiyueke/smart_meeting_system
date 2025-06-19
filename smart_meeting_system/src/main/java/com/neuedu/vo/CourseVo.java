package com.neuedu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseVo {
    private int id;
    private String name;
    private String description;
    private String author;
    private String videoUrl;
    private String coverUrl;
}
