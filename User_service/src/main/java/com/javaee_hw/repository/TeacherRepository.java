package com.javaee_hw.repository;

import com.javaee_hw.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {

    List<Teacher> findByTnameContaining(String tname);
}