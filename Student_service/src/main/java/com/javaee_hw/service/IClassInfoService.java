package com.javaee_hw.service; // 修正包名

import com.javaee_hw.entity.ClassInfo;
import java.util.List;

public interface IClassInfoService {
    List<ClassInfo> getAllClasses();
}