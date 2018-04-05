package com.jacky.sams.service;

import com.jacky.sams.dao.SysUserRepository;
import com.jacky.sams.entity.SysRole;
import com.jacky.sams.entity.SysUser;

import javax.persistence.criteria.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SysUserService {

    @Resource
    private SysUserRepository userRepository;

    public void addUser(SysUser user){
        userRepository.save(user);
    }

    public SysUser getUser(String username){
        return userRepository.findByUsername(username);
    }

    public Page<SysUser> findAllByPage(int pageIndex, int pageSize){
        return userRepository.findAll(new PageRequest(pageIndex-1,pageSize));
    }

    public Page<SysUser> findAllAssoManagerByPage(HashMap<String ,Object> hashMap,int pageIndex, int pageSize){
        Specification querySpecifi = (Specification<SysUser>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.join("role").get("id"), "3"));
            if(null != hashMap.get("username") && !hashMap.get("username").equals("")){
                predicates.add(criteriaBuilder.equal(root.get("username"), (String) hashMap.get("username")));
            }
            if(null != hashMap.get("asso_id") && !hashMap.get("asso_id").equals("")){
                predicates.add(criteriaBuilder.equal(root.join("associationDetail").get("id"), (String) hashMap.get("asso_id")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return userRepository.findAll(querySpecifi,new PageRequest(pageIndex-1,pageSize));
    }

    public void deleteById(String ids){
        String[] id=ids.split(",");
        for (String anId : id) {
            userRepository.deleteById(anId);
        }
    }
}
