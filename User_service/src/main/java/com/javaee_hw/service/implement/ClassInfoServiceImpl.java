package com.javaee_hw.service.implement;

import com.javaee_hw.entity.ClassInfo;
import com.javaee_hw.repository.ClassInfoRepository;
import com.javaee_hw.service.IClassInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassInfoServiceImpl implements IClassInfoService {

    @Autowired
    private ClassInfoRepository classInfoRepository;

    @Override
    public List<ClassInfo> getAllClasses() {
        return classInfoRepository.findAll();
    }

    @Override
    @Transactional
    public void addClass(ClassInfo classInfo) {
        String classNo = classInfo.getClassno();
        if (classNo == null || classNo.isBlank()) {
            throw new IllegalArgumentException("班级号不能为空");
        }
        if (classInfoRepository.existsById(classNo)) {
            throw new RuntimeException("班级号已存在");
        }
        classInfoRepository.save(classInfo);
    }

    @Override
    @Transactional
    public void updateClass(ClassInfo classInfo) {
        String classNo = classInfo.getClassno();
        if (classNo == null || classNo.isBlank()) {
            throw new IllegalArgumentException("班级号不能为空");
        }
        if (!classInfoRepository.existsById(classNo)) {
            throw new RuntimeException("班级不存在，无法修改");
        }
        classInfoRepository.save(classInfo);
    }

    @Override
    @Transactional
    public void deleteClass(String classno) {
        classInfoRepository.deleteById(classno);
    }
}
