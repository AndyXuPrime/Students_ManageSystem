package com.javaee_hw.repository;

import com.sims.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository<实体类, 主键类型>
@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    // 魔法方法：Spring 会自动根据方法名生成 SQL: select * from student where sname like ?
    Page<Student> findBySnameContaining(String sname, Pageable pageable);
}