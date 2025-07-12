    package com.neuedu.pojo;

    import com.baomidou.mybatisplus.annotation.IdType;
    import com.baomidou.mybatisplus.annotation.TableField;
    import com.baomidou.mybatisplus.annotation.TableId;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Dept {
        @TableId(value = "dept_id", type = IdType.AUTO)
        private Integer deptId;
        // 上级部门ID（树形结构）
        private Integer parentId;

        private String deptName;
        // 显示排序
        private Integer orderNum;
        // 负责人
        private String leader;
        // 联系电话
        private String phone;
        // 邮箱
        private String email;
        // 状态（正常，停用）
        private String status;
        // 创建时间
        private LocalDateTime createTime;

    }
