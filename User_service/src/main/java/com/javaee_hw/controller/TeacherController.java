package com.javaee_hw.controller;

import com.javaee_hw.common.Result;
import com.javaee_hw.entity.Teacher;
import com.javaee_hw.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    private ITeacherService teacherService;

    // 查询列表
    @GetMapping("/list")
    public Result<List<Teacher>> list() {
        return Result.success(teacherService.getAllTeachers());
    }

    // 新增
    @PostMapping("/add")
    public Result<?> add(@RequestBody Teacher teacher) {
        try {
            teacherService.addTeacher(teacher);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("新增教师失败: " + e.getMessage());
        }
    }

    // 修改
    @PutMapping("/update")
    public Result<?> update(@RequestBody Teacher teacher) {
        try {
            teacherService.updateTeacher(teacher);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("修改教师失败: " + e.getMessage());
        }
    }

    // 删除
    @DeleteMapping("/delete/{tno}")
    public Result<?> delete(@PathVariable String tno) {
        try {
            teacherService.deleteTeacher(tno);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("删除教师失败: " + e.getMessage());
        }
    }
}