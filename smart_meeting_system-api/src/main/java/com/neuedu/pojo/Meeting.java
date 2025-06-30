package com.neuedu.pojo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 智能会议系统中会议的实体类
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class Meeting {
    /**
     * ID
     */
    private Integer id;
    /**
     * 会议名称
     */
    private String name;
    /**
     * 会议创建者
     */
    private String creator;
    /**
     * 当前会议的状态
     * 0：过期无效
     * 1：有效
     */
    private Integer isEffective;
    /**
     * 会议简介内容
     */
    private String content;
    /**
     * 封面
     */
    private String cover;
    /**
     *会议开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    /**
     *会议结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 是否删除的标记
     */
    private Integer isDeleted;
}
