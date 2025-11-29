package com.javaee_hw.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "student")
public class Student {
    @Id
    @Column(name = "Sno", length = 20)
    private String sno;

    @Column(name = "Sname")
    private String sname;

    @Column(name = "Sex")
    private String sex;

    @Column(name = "Birth")
    @Temporal(TemporalType.DATE)
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