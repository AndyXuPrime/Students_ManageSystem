package com.javaee_hw.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "student_table") // 对应你的表名
public class Student {

    @Id
    @Column(name = "Sno", length = 8) // 对应 char(8)
    private String sno;

    @Column(name = "Sname", length = 8) // 对应 varchar(8)
    private String sname;

    @Column(name = "Sex", length = 2) // 对应 char(2)
    private String sex;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "Birth") // 对应 datetime
    @Temporal(TemporalType.TIMESTAMP) // 使用 TIMESTAMP 或 DATE 都可以适配 datetime
    private Date birth;

    @Column(name = "Classno", length = 3) // 对应 char(3)
    private String classno;

    // 👇 关键修改：添加入学时间映射
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "Entrance_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entranceDate;

    @Column(name = "Home_addr", length = 40)
    private String homeAddr;

    @Column(name = "Sdept", length = 20) // 必填
    private String sdept;

    @Column(name = "Postcode", length = 6)
    private String postcode;
}