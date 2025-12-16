package com.javaee_hw.resource.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

/**
 * MySQL 实体：用于持久化存储
 */
@Data
@Entity
@Table(name = "course_resource")
public class CourseResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type", length = 20)
    private String fileType;

    @Column(name = "file_url", length = 500, nullable = false)
    private String fileUrl;

    @Column(name = "course_name", length = 100)
    private String courseName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "upload_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadTime;
}