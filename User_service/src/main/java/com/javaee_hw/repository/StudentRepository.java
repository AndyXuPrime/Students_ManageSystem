package com.javaee_hw.repository;

import com.javaee_hw.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    @Query("SELECT s FROM Student s WHERE s.sname LIKE %?1% OR s.classno LIKE %?1% OR s.sdept LIKE %?1%")
    Page<Student> findByKeyword(String keyword, Pageable pageable);
}