package com.jacky.sams.service;

import com.jacky.sams.dao.AssociationDetailRepository;
import com.jacky.sams.dao.SysUserRepository;
import com.jacky.sams.entity.AssociationDetail;
import com.jacky.sams.entity.SysUser;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class AssociationService {

    @Resource
    private AssociationDetailRepository associationDetailRepository;

    @Resource
    private SysUserRepository userRepository;

    public Page<AssociationDetail> findAllByPage(int pageIndex,int pageSize){
        return associationDetailRepository.findAll(new PageRequest(pageIndex-1,pageSize));
    }

    public void deleteById(String ids){
        String[] id=ids.split(",");
        for (String anId : id) {
            List<SysUser> users=userRepository.findAllByAssociationDetail_Id(anId);
            userRepository.deleteAll(users);
            associationDetailRepository.deleteById(anId);
        }
    }

    public List<AssociationDetail> getAssociation(){
        Specification querySpecifi = (Specification<AssociationDetail>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("pass").as(Integer.class), 1));
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return associationDetailRepository.findAll(querySpecifi);
    }

    public AssociationDetail getAssociation(String id){
        return associationDetailRepository.findById(id).orElse(null);
    }

    public Page<AssociationDetail> findAllAssociationByPage(HashMap<String ,Object> hashMap, int pageIndex, int pageSize){
        Specification querySpecifi = (Specification<AssociationDetail>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(null != hashMap.get("category") && !hashMap.get("category").equals("")){
                predicates.add(criteriaBuilder.equal(root.get("category"), (String) hashMap.get("category")));
            }
            if(null != hashMap.get("name") && !hashMap.get("name").equals("")){
                predicates.add(criteriaBuilder.like(root.get("name"), "%"+(String) hashMap.get("name")+"%"));
            }
            if(null != hashMap.get("pass") && !hashMap.get("pass").equals("")){
                predicates.add(criteriaBuilder.equal(root.get("pass").as(Integer.class), (Integer) hashMap.get("pass")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return associationDetailRepository.findAll(querySpecifi,new PageRequest(pageIndex-1,pageSize));
    }

    public void addAssociation(AssociationDetail detail){
        associationDetailRepository.save(detail);
    }

    public void pass(String id,Integer passCode){
        AssociationDetail detail=associationDetailRepository.findById(id).get();
        detail.setPass(passCode);
        associationDetailRepository.save(detail);
    }
}
