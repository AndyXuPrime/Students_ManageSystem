package com.javaee_hw.controller;


import com.sims.common.Result;
import com.sims.entity.ClassInfo;
import com.sims.service.IClassInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/class")
public class ClassController {

    @Autowired
    private IClassInfoService classInfoService;

    @GetMapping("/list")
    public Result<List<ClassInfo>> listClasses() {
        return Result.success(classInfoService.list());
    }
}