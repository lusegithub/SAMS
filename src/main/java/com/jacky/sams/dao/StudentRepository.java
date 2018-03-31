package com.jacky.sams.dao;

import com.jacky.sams.entity.Student;
import com.jacky.sams.vo.StudentAssociationVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, String>,JpaSpecificationExecutor<Student> {

    Student findByUser_Id(String userId);
}
