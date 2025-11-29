package com.javaee_hw.controller;

import com.javaee_hw.config.Result;
import com.javaee_hw.entity.*;
import com.javaee_hw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private IStudentService studentService;

    @GetMapping("/page")
    public Result<Page<Student>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        // 直接调用 Service
        return Result.success(studentService.getStudentPage(current, size, name));
    }

    @PostMapping
    public Result<Student> save(@RequestBody Student student) {
        
        if (studentService.getStudentById(student.getSno()) != null) {
            return Result.error("学号已存在");
        }
        return Result.success(studentService.saveStudent(student));
    }

    @PutMapping
    public Result<Student> update(@RequestBody Student student) {
        return Result.success(studentService.saveStudent(student));
    }

    @DeleteMapping("/{sno}")
    public Result<String> delete(@PathVariable String sno) {
        studentService.deleteStudent(sno);
        return Result.success("删除成功");
    }
    
    @GetMapping("/{sno}")
    public Result<Student> getOne(@PathVariable String sno) {
        return Result.success(studentService.getStudentById(sno));
    }
}