package com.jacky.sams.service;

import com.jacky.sams.dao.SysUserRepository;
import com.jacky.sams.entity.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SysUserService {

    @Resource
    private SysUserRepository userRepository;

    public void addUser(SysUser user){
        userRepository.save(user);
    }
}
