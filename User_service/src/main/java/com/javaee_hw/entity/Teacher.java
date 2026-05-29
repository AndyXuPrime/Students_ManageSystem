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

    public String getTno() {
        return tno;
    }

    public void setTno(String tno) {
        this.tno = tno;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
