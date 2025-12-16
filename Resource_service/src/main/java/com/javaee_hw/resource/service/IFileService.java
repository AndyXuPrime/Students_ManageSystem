package com.javaee_hw.resource.service;

import com.javaee_hw.resource.entity.CourseResource; // 改用 MySQL 实体
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface IFileService {
    String upload(MultipartFile file, String courseName);

    // 返回值类型改为 CourseResource
    List<CourseResource> search(String keyword);

    void delete(Long id);
}