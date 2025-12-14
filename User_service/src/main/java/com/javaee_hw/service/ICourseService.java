package com.javaee_hw.service;
import com.javaee_hw.entity.Course;
import java.util.List;

public interface ICourseService {
    List<Course> getAllCourses();
    void addCourse(Course course);
    void updateCourse(Course course);
    void deleteCourse(String cno);
}