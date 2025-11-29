package com.javaee_hw.repository;

import com.javaee_hw.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassInfoRepository extends JpaRepository<ClassInfo, Integer> {
    // 这里不需要写代码，JpaRepository 已经包含了 findAll, save, delete 等方法
}