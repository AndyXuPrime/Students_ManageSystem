package com.javaee_hw.service;

import com.javaee_hw.entity.ClassInfo;
import java.util.List;

public interface IClassInfoService {
    List<ClassInfo> getAllClasses();
    void addClass(ClassInfo classInfo);     // 新增
    void updateClass(ClassInfo classInfo);  // 修改
    void deleteClass(String classno);       // 删除
}