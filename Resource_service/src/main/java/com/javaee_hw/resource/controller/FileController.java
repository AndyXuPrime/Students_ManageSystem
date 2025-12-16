package com.javaee_hw.resource.controller;

import com.javaee_hw.resource.entity.CourseResource; // 改用 MySQL 实体
import com.javaee_hw.resource.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private IFileService fileService;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "courseName", defaultValue = "未知课程") String courseName) {
        String url = fileService.upload(file, courseName);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "上传成功");
        result.put("url", url);
        return result;
    }

    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam("keyword") String keyword) {
        // 返回类型变了
        List<CourseResource> list = fileService.search(keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        try {
            fileService.delete(id);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "删除成功");
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("msg", "删除失败: " + e.getMessage());
            return result;
        }
    }
}