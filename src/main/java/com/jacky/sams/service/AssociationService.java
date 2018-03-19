package com.jacky.sams.service;

import com.jacky.sams.dao.AssociationDetailRepository;
import com.jacky.sams.entity.AssociationDetail;
import com.jacky.sams.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssociationService {

    @Autowired
    AssociationDetailRepository associationDetailRepository;

    public void addAdminUser(AssociationDetail detail){
//        Optional<AssociationDetail> detail=associationDetailRepository.findById(associationId);
//        if (detail.isPresent()){
//            AssociationDetail associationDetail=detail.get();
//            List<SysUser> userList=associationDetail.getUsers();
//            userList.add(user);
//            associationDetail.setUsers(userList);
//            associationDetailRepository.save(associationDetail);
//        }
        associationDetailRepository.save(detail);
        detail.getId();
    }

    public Page<AssociationDetail> findAllByPage(int pageIndex,int pageSize){
        return associationDetailRepository.findAll(new PageRequest(pageIndex-1,pageSize));
    }

    public void deleteById(String ids){
        String[] id=ids.split(",");
        for (String anId : id) {
            associationDetailRepository.deleteById(anId);
        }
    }

}
