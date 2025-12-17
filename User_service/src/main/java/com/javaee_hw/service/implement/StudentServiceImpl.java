package com.javaee_hw.service.implement;

import com.javaee_hw.entity.Student;
import com.javaee_hw.repository.StudentRepository;
import com.javaee_hw.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements IStudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Page<Student> getStudentPage(Integer current, Integer size, String keyword) {
        // 防止空指针，给默认值
        if (current == null) current = 1;
        if (size == null) size = 10;

        Pageable pageable = PageRequest.of(current - 1, size);

        if (keyword != null && !keyword.isEmpty()) {
            // 这里调用 StudentRepository 中新写的 findByKeyword
            return studentRepository.findByKeyword(keyword, pageable);
        } else {
            return studentRepository.findAll(pageable);
        }
    }

    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(String sno) {
        studentRepository.deleteById(sno);
    }

    @Override
    public Student getStudentById(String sno) {
        return studentRepository.findById(sno).orElse(null);
    }
}