package com.javaee_hw.service.implement;

import com.javaee_hw.entity.Teacher;
import com.javaee_hw.repository.TeacherRepository;
import com.javaee_hw.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TeacherServiceImpl implements ITeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    @Transactional
    public void addTeacher(Teacher teacher) {
        String tno = teacher.getTno();
        if (tno == null || tno.isBlank()) {
            throw new IllegalArgumentException("教师工号不能为空");
        }
        if (teacherRepository.existsById(tno)) {
            throw new RuntimeException("教师工号已存在");
        }
        teacherRepository.save(teacher);
    }

    @Override
    @Transactional
    public void updateTeacher(Teacher teacher) {
        String tno = teacher.getTno();
        if (tno == null || tno.isBlank()) {
            throw new IllegalArgumentException("教师工号不能为空");
        }
        if (!teacherRepository.existsById(tno)) {
            throw new RuntimeException("教师不存在");
        }
        teacherRepository.save(teacher);
    }

    @Override
    @Transactional
    public void deleteTeacher(String tno) {
        teacherRepository.deleteById(tno);
    }
}
