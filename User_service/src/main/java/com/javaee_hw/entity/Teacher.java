package com.javaee_hw.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "teacher")
public class Teacher {
    @Id
    @Column(name = "tno", length = 20)
    private String tno;

    @Column(name = "tname", length = 50)
    private String tname;

    @Column(name = "title", length = 20)
    private String title;
}