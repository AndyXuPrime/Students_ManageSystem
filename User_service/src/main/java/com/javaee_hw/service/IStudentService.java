package com.javaee_hw.service;

import com.javaee_hw.entity.Student;
import org.springframework.data.domain.Page;

public interface IStudentService {
    Page<Student> getStudentPage(Integer current, Integer size, String keyword);

    Student saveStudent(Student student);
    void deleteStudent(String sno);

    Student getStudentById(String sno);
}