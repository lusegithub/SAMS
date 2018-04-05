package com.jacky.sams.service;

import com.jacky.sams.dao.StudentRepository;
import com.jacky.sams.dao.impl.CommonDao;
import com.jacky.sams.entity.Student;
import com.jacky.sams.vo.StudentAssociationVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StudentService {

    @Resource
    private StudentRepository repository;

    @Resource
    private CommonDao commonDao;

    public void addStudent(Student student){
        repository.save(student);
    }

    public Student findStudentByUserId(String userId){
        return repository.findByUser_Id(userId);
    }

    public Student findOne(String id){
        return repository.findById(id).orElse(null);
    }

    public Page<Student> findAllStudentByPage(HashMap<String ,Object> hashMap, int pageIndex, int pageSize){
        Specification querySpecifi = (Specification<Student>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(null != hashMap.get("association_id") && !hashMap.get("association_id").equals("")){
                predicates.add(criteriaBuilder.equal(root.join("studentAssociations").join("association").get("id"), (String) hashMap.get("association_id")));
            }
            if(!StringUtils.isEmpty(hashMap.get("name"))){
                predicates.add(criteriaBuilder.like(root.get("name"), "%"+hashMap.get("name")+"%"));
            }
            if(null != hashMap.get("stuNo") && !hashMap.get("stuNo").equals("")){
                predicates.add(criteriaBuilder.equal(root.get("stuNo"), hashMap.get("stuNo")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return repository.findAll(querySpecifi,new PageRequest(pageIndex-1,pageSize));
    }

    public Page<Student> findStudentsByAssociation(HashMap<String ,Object> hashMap, int pageIndex, int pageSize){
        String fromSql="FROM student s JOIN student_association sa ON s.id=sa.student_id ";
        String whereSql="WHERE sa.association_id=:association_id";
        if (!StringUtils.isEmpty(hashMap.get("name"))){
            whereSql+=" and s.name like :name";
        }
        if (!StringUtils.isEmpty(hashMap.get("status"))){
            whereSql+=" and sa.status=:status";
        }
        String sql="SELECT s.*,sa.apply_time,sa.enter_time,sa.status "+fromSql+whereSql;
        String countsql="SELECT count(*) "+fromSql+whereSql;
        List vos= commonDao.queryListEntity(sql,hashMap,StudentAssociationVo.class);
        if (vos==null){
            vos=new ArrayList();
        }
        int total=commonDao.getCountBy(countsql,hashMap);
        return new PageImpl<Student>(vos,new PageRequest(pageIndex-1,pageSize),total);
    }

    public void updateStatus(String ids,String assoId){
        String sql="UPDATE student_association sa SET sa.status=1,sa.enter_time=:enterTime WHERE sa.student_id in (:stuId) and sa.association_id=:assoId and sa.status=2";
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("assoId",assoId);
        String[] id=ids.split(",");
        paramMap.put("stuId", Arrays.asList(id));
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        paramMap.put("enterTime",formatter.format(date));
        commonDao.deleteOrUpDate(sql,paramMap);
    }

    public void deleteById(String ids){
        String[] id=ids.split(",");
        for (String anId : id) {
            repository.deleteById(anId);
        }
    }
}
