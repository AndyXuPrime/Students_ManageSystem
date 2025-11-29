package com.javaee_hw.repository;

import com.javaee_hw.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// 👇 关键修改：将 <ClassInfo, Integer> 改为 <ClassInfo, String>
public interface ClassInfoRepository extends JpaRepository<ClassInfo, String> {
}