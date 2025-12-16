package com.javaee_hw.resource.repository;

import com.javaee_hw.resource.entity.CourseResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<CourseResource, Long> {

    List<CourseResource> findByFileNameContainingOrCourseNameContaining(String fileName, String courseName);
}