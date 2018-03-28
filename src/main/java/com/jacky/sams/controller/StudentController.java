package com.jacky.sams.controller;

import com.jacky.sams.entity.Student;
import com.jacky.sams.entity.SysRole;
import com.jacky.sams.entity.SysUser;
import com.jacky.sams.service.StudentService;
import com.jacky.sams.service.SysRoleService;
import com.jacky.sams.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;

    @Resource
    private SysRoleService roleService;

    @Resource
    private SysUserService userService;

    @PostMapping("/signup")
    @ResponseBody
    public void signUp(Student student, SysUser user){
        SysRole role=roleService.getRole("2");
        user.setRole(role);
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        user.setSignUpTime(formatter.format(date));
        userService.addUser(user);
        student.setUser(user);
        studentService.addStudent(student);
    }

    @RequestMapping(value = "/index")
    public String index(){
        return "student/index";
    }

    @RequestMapping(value = "/detail")
    public String detail(Model model){
        return "student/detail";
    }
}
