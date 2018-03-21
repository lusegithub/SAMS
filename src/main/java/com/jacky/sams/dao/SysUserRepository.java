package com.jacky.sams.dao;

import com.jacky.sams.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SysUserRepository extends JpaRepository<SysUser, String> {
    SysUser findByUsername(String username);

    @Query(value = "select u from SysUser u where u.role.id='3'")
    Page<SysUser> findAllAssoManagerByPage(Pageable pageable);
}
