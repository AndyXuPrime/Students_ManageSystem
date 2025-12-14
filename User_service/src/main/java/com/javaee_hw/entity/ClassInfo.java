package com.javaee_hw.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "class_info") // 对应数据库表名
public class ClassInfo {

    @Id // 主键是 Classno
    @Column(name = "Classno", length = 3)
    private String classno; // 对应数据库 CHAR(3)

    @Column(name = "Major", length = 20)
    private String major;   // 对应数据库 VARCHAR(20)
}