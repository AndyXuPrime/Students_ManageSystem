package com.javaee_hw.service.implement;

import com.javaee_hw.entity.Student;
import com.javaee_hw.repository.StudentRepository;
import com.javaee_hw.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        // 1. 自动填充入学时间 (如果前端没传，就默认当前时间)
        if (student.getEntranceDate() == null) {
            student.setEntranceDate(new Date());
        }

        // 2. 自动填充院系 (如果没传，给个默认值)
        if (student.getSdept() == null || student.getSdept().isEmpty()) {
            student.setSdept("计算机系");
        }

        // 3. 截断过长的字段 (防止数据库报错)
        if (student.getClassno() != null && student.getClassno().length() > 3) {
            throw new RuntimeException("班级编号不能超过3位！");
        }
        if (student.getSname() != null && student.getSname().length() > 8) {
            throw new RuntimeException("姓名太长了(最多8字符)！");
        }
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