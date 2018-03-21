package com.jacky.sams.dao;

import com.jacky.sams.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SysUserRepository extends JpaRepository<SysUser, String>,JpaSpecificationExecutor<SysUser> {
    SysUser findByUsername(String username);

    @Query(value = "select u from SysUser u where u.role.id='3' and u.username=:username")
    Page<SysUser> findAllAssoManagerByPage(@Param("username") String username, Pageable pageable);
}
