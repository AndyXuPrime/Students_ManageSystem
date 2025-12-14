package com.javaee_hw.service.impl;

import com.javaee_hw.entity.Course;
import com.javaee_hw.repository.CourseRepository;
import com.javaee_hw.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseServiceImpl implements ICourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    @Transactional
    public void addCourse(Course course) {
        if (courseRepository.existsById(course.getCno())) {
            throw new RuntimeException("课程号已存在");
        }
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void updateCourse(Course course) {
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void deleteCourse(String cno) {
        courseRepository.deleteById(cno);
    }
}