package com.jacky.sams.controller;

import com.jacky.sams.dao.AdminRepository;
import com.jacky.sams.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    AdminRepository adminRepository;

    @RequestMapping(value = "/save")
    public void addAdmin(){
        Admin admin=new Admin();
        admin.setUsername("admin");
        admin.setPassword("123456");
        adminRepository.save(admin);
    }
}
