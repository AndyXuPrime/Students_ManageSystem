package com.sims.entity;

import lombok.Data;
import javax.persistence.*; // 注意是 javax.persistence
import java.util.Date;

@Data
@Entity // 告诉 JPA 这是一个实体
@Table(name = "student") // 对应数据库表名
public class Student {

    @Id // 主键
    @Column(name = "Sno", length = 20)
    private String sno; // 学号是手动输入的字符串，不需要 @GeneratedValue

    @Column(name = "Sname")
    private String sname;

    @Column(name = "Sex")
    private String sex;

    @Column(name = "Birth")
    @Temporal(TemporalType.DATE) // 指定日期格式
    private Date birth;

    @Column(name = "Classno")
    private String classno;

    @Column(name = "Entrance_date")
    @Temporal(TemporalType.DATE)
    private Date entranceDate;

    @Column(name = "Home_addr")
    private String homeAddr;

    @Column(name = "Sdept")
    private String sdept;

    @Column(name = "Postcode")
    private String postcode;
}