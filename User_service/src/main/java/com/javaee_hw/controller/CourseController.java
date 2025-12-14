package com.javaee_hw.controller;

import com.javaee_hw.common.Result;
import com.javaee_hw.entity.Course;
import com.javaee_hw.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private ICourseService courseService;

    @GetMapping("/list")
    public Result<List<Course>> list() {
        return Result.success(courseService.getAllCourses());
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Course course) {
        try {
            // 前端传来的 JSON 格式应该是:
            // { "cno": "CS101", "cname": "...", "teacher": { "tno": "T001" } }
            courseService.addCourse(course);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("新增课程失败: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Course course) {
        try {
            courseService.updateCourse(course);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("修改课程失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{cno}")
    public Result<?> delete(@PathVariable String cno) {
        try {
            courseService.deleteCourse(cno);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("删除课程失败: " + e.getMessage());
        }
    }
}