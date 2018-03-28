package com.jacky.sams.service;

import com.jacky.sams.dao.StudentRepository;
import com.jacky.sams.entity.Student;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StudentService {

    @Resource
    private StudentRepository repository;

    public void addStudent(Student student){
        repository.save(student);
    }
}
