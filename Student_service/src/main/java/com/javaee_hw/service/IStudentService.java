package com.javaee_hw.service; // 修正包名

import com.javaee_hw.entity.Student;
import org.springframework.data.domain.Page;

public interface IStudentService {
    Page<Student> getStudentPage(int page, int size, String name);
    Student saveStudent(Student student);
    void deleteStudent(String sno);
    Student getStudentById(String sno);
}