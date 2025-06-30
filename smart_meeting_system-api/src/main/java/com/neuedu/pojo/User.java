package com.neuedu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    @TableId
    private Long id;

    private String username;

    private String password;

    private String role;

    private String enterpriseName;

    private String enterprisePhone;

    private String adminCode;

    private static final long serialVersionUID = 1L;
}