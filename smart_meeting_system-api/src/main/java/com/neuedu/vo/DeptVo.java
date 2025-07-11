package com.neuedu.vo;

import lombok.Data;

@Data
public class DeptVo {
    private Integer deptId;
    private Integer parentId;
    private String deptName;
    private Integer orderNum;
    private String leader;
    private String phone;
    private String email;
    private String status;
}

