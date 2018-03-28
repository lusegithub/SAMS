package com.jacky.sams.dao;

import com.jacky.sams.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentRepository extends JpaRepository<Student, String>,JpaSpecificationExecutor<Student> {
}
