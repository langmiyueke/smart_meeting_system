package com.neuedu.pojo;


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

    private String title;

    private String imageUrl;

    private String author;

    private String summary;

    private String content;

    private Long tenantId;

    private static final long serialVersionUID = 1L;


}