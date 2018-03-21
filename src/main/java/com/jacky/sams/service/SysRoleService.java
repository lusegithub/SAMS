package com.jacky.sams.service;

import com.jacky.sams.dao.SysRoleRepository;
import com.jacky.sams.entity.SysRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class SysRoleService {

    @Resource
    private SysRoleRepository roleRepository;

    public SysRole getRole(String id){
        Optional<SysRole> role=roleRepository.findById(id);
        return role.orElse(null);
    }
}
