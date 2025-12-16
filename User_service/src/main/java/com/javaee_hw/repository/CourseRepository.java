package com.javaee_hw.repository;

import com.javaee_hw.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
     List<Course> findByCnameContaining(String cname);
}