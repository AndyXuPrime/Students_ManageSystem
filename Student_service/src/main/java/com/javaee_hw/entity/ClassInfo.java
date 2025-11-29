package com.javaee_hw.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "class_info")
public class ClassInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "class_no")
    private String classNo; 
    
    @Column(name = "class_name")
    private String className; 
}