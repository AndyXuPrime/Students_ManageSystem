package com.javaee_hw.service;
import com.javaee_hw.entity.Teacher;
import java.util.List;

public interface ITeacherService {
    List<Teacher> getAllTeachers();
    void addTeacher(Teacher teacher);    // 新增
    void updateTeacher(Teacher teacher); // 修改
    void deleteTeacher(String tno);      // 删除
}