package com.javaee_hw.repository;

import com.javaee_hw.entity.Student; // 确保这里引用的是你项目里的 Student
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    Page<Student> findBySnameContaining(String sname, Pageable pageable);
}