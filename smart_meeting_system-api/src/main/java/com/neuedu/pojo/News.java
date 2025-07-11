package com.neuedu.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName news
 */
@TableName(value ="news")
@Data
public class News implements Serializable {
    @TableId
    private Long id;
    @ExcelProperty("新闻标题")
    private String title;
    @ExcelProperty("图片地址")
    private String imageUrl;
    @ExcelProperty("作者")
    private String author;
    @ExcelProperty("新闻简介")
    private String summary;
    @ExcelProperty("内容")
    private String content;
    @ExcelProperty("租户ID")
    private Long tenantId;

    private static final long serialVersionUID = 1L;


}