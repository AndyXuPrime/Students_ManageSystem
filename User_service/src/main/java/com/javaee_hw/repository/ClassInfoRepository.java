package com.javaee_hw.repository;

import com.javaee_hw.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassInfoRepository extends JpaRepository<ClassInfo, String> {
}