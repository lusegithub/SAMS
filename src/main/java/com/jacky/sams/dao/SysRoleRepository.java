package com.jacky.sams.dao;

import com.jacky.sams.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysRoleRepository extends JpaRepository<SysRole, String> {

    SysRole findByName(String name);
}
