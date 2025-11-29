package com.javaee_hw.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("class_info") // 假设数据库表名为 class_info
public class ClassInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("class_no")
    private String classNo; // 班级编号，如 "101"
    
    @TableField("class_name")
    private String className; // 班级名称，如 "计算机一班"
}