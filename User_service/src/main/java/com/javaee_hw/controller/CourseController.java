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
    public Result<List<Course>> list(@RequestParam(required = false) String keyword) {
        // 如果有关键词，调用搜索方法；否则查所有
        // 注意：需要在 Service 层实现 searchCourses(keyword) 调用 repository.findByKeyword(keyword)
        if (keyword != null && !keyword.isEmpty()) {
            return Result.success(courseService.searchCourses(keyword));
        }
        return Result.success(courseService.getAllCourses());
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Course course) {
        try { courseService.addCourse(course); return Result.success(null); }
        catch (Exception e) { return Result.error("新增课程失败: " + e.getMessage()); }
    }
    @PutMapping("/update")
    public Result<?> update(@RequestBody Course course) {
        try { courseService.updateCourse(course); return Result.success(null); }
        catch (Exception e) { return Result.error("修改课程失败: " + e.getMessage()); }
    }
    @DeleteMapping("/delete/{cno}")
    public Result<?> delete(@PathVariable String cno) {
        try { courseService.deleteCourse(cno); return Result.success(null); }
        catch (Exception e) { return Result.error("删除课程失败: " + e.getMessage()); }
    }
}