package com.javaee_hw.service.implement; // 修正包名

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
    public Page<Student> getStudentPage(int page, int size, String name) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (name != null && !name.isEmpty()) {
            return studentRepository.findBySnameContaining(name, pageable);
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