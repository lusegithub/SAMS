package com.jacky.sams.controller;

import com.jacky.sams.dao.SysUserRepository;
import com.jacky.sams.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    SysUserRepository sysUserRepository;

    @RequestMapping(value = "/get")
    @ResponseBody
    public SysUser get(){
        SysUser user=sysUserRepository.findByUsername("admin");
        return user;
    }
}
