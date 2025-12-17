package com.javaee_hw.service.implement;

import com.javaee_hw.entity.Course;
import com.javaee_hw.repository.CourseRepository;
import com.javaee_hw.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public List<Course> searchCourses(String keyword) {
        // 调用 Repository 中定义的 findByKeyword
        return courseRepository.findByKeyword(keyword);
    }

    @Override
    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void updateCourse(Course course) {
        courseRepository.save(course);
    }

    @Override
    public void deleteCourse(String cno) {
        courseRepository.deleteById(cno);
    }
}