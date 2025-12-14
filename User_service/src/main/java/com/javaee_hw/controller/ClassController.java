package com.javaee_hw.controller;

import com.javaee_hw.common.Result;
import com.javaee_hw.entity.ClassInfo;
import com.javaee_hw.service.IClassInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
public class ClassController {
    @Autowired
    private IClassInfoService classInfoService;

    // 查询所有
    @GetMapping("/list")
    public Result<List<ClassInfo>> listClasses() {
        return Result.success(classInfoService.getAllClasses());
    }

    // 新增班级
    @PostMapping("/add")
    public Result<?> addClass(@RequestBody ClassInfo classInfo) {
        try {
            classInfoService.addClass(classInfo);
            // 👇 修改点：传入 null，满足 Result.success(T data) 的参数要求
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("新增失败: " + e.getMessage());
        }
    }

    // 修改班级
    @PutMapping("/update")
    public Result<?> updateClass(@RequestBody ClassInfo classInfo) {
        try {
            classInfoService.updateClass(classInfo);
            // 👇 修改点：传入 null
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("修改失败: " + e.getMessage());
        }
    }

    // 删除班级
    @DeleteMapping("/delete/{classno}")
    public Result<?> deleteClass(@PathVariable String classno) {
        try {
            classInfoService.deleteClass(classno);
            // 👇 修改点：传入 null
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}