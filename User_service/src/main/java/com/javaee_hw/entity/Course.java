package com.javaee_hw.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "course")
public class Course {
    @Id
    @Column(name = "cno", length = 20)
    private String cno;

    @Column(name = "cname", length = 100)
    private String cname;

    @Column(name = "credit")
    private Integer credit;

    @Column(name = "period")
    private Integer period;

    @Column(name = "type", length = 20)
    private String type; // 必修/选修

    // 重点：建立与 Teacher 的多对一关系
    @ManyToOne
    @JoinColumn(name = "tno") // 数据库中的外键列名
    private Teacher teacher;
}