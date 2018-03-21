package com.jacky.sams.service;

import com.jacky.sams.dao.SysUserRepository;
import com.jacky.sams.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SysUserService {

    @Resource
    private SysUserRepository userRepository;

    public void addUser(SysUser user){
        userRepository.save(user);
    }

    public SysUser getUser(){
        return userRepository.findByUsername("test");
    }

    public Page<SysUser> findAllByPage(int pageIndex, int pageSize){
        return userRepository.findAll(new PageRequest(pageIndex-1,pageSize));
    }

    public Page<SysUser> findAllAssoManagerByPage(int pageIndex, int pageSize){
        return userRepository.findAllAssoManagerByPage(new PageRequest(pageIndex-1,pageSize));
    }

    public void deleteById(String ids){
        String[] id=ids.split(",");
        for (String anId : id) {
            userRepository.deleteById(anId);
        }
    }
}
