package com.jacky.sams.service;

import com.jacky.sams.dao.AssociationDetailRepository;
import com.jacky.sams.dao.SysUserRepository;
import com.jacky.sams.entity.AssociationDetail;
import com.jacky.sams.entity.StudentAssociation;
import com.jacky.sams.entity.SysUser;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.text.SimpleDateFormat;
import java.util.*;

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
            //构造查询语句
            List<Predicate> predicates = new ArrayList<>();
            //判断是否根据类别查询
            if(null != hashMap.get("category") && !hashMap.get("category").equals("")){
                predicates.add(criteriaBuilder.equal(root.get("category"), (String) hashMap.get("category")));
            }
            //判断是否根据名称查询
            if(null != hashMap.get("name") && !hashMap.get("name").equals("")){
                predicates.add(criteriaBuilder.like(root.get("name"), "%"+(String) hashMap.get("name")+"%"));
            }
            //判断是否根据状态查询
            if(null != hashMap.get("pass") && !hashMap.get("pass").equals("")){
                predicates.add(criteriaBuilder.equal(root.get("pass").as(Integer.class), (Integer) hashMap.get("pass")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //返回搜索结果
        return associationDetailRepository.findAll(querySpecifi,new PageRequest(pageIndex-1,pageSize));
    }

    public void addAssociation(AssociationDetail detail){
        associationDetailRepository.save(detail);
    }

    public void pass(String id,Integer passCode){
        //获取选中的社团
        AssociationDetail detail=associationDetailRepository.findById(id).get();
        //设置审核结果
        detail.setPass(passCode);
        //如果审核通过，设置审核通过的时间
        if (passCode==1){
            Date date=new Date();
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            detail.setBuildTime(formatter.format(date));
        }
        associationDetailRepository.save(detail);//更新数据库信息
    }

    public void save(AssociationDetail detail){
        associationDetailRepository.save(detail);
    }

    public List<AssociationDetail> findAllAssociationByStudent(HashMap<String ,Object> hashMap){
        Specification querySpecifi = (Specification<AssociationDetail>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<AssociationDetail,StudentAssociation> join=root.join("studentAssociations");
            if (!StringUtils.isEmpty(hashMap.get("status"))){
                predicates.add(criteriaBuilder.equal(join.get("status").as(Integer.class), hashMap.get("status")));
            }
            if(null != hashMap.get("studentId") && !hashMap.get("studentId").equals("")){
                predicates.add(criteriaBuilder.equal(join.get("student").get("id"), hashMap.get("studentId")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return associationDetailRepository.findAll(querySpecifi);
    }

    public AssociationDetail findByName(String name){
        return associationDetailRepository.findByName(name);
    }
}
