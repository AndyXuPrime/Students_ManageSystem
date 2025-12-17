package com.javaee_hw.repository;

import com.javaee_hw.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

     @Query("SELECT c FROM Course c LEFT JOIN c.teacher t WHERE c.cname LIKE %?1% OR c.cno LIKE %?1% OR t.tname LIKE %?1%")
     List<Course> findByKeyword(String keyword);
}