package com.jacky.sams.service;

import com.jacky.sams.dao.ActivityRepository;
import com.jacky.sams.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ActivityService {

    @Resource
    private ActivityRepository activityRepository;

    public Page<Activity> findAllActivityByPage(HashMap<String ,Object> hashMap, int pageIndex, int pageSize){
        Specification querySpecifi = (Specification<Activity>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(null != hashMap.get("name") && !hashMap.get("name").equals("")){
                predicates.add(criteriaBuilder.like(root.get("name"), "%"+(String) hashMap.get("name")+"%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return activityRepository.findAll(querySpecifi,new PageRequest(pageIndex-1,pageSize));
    }

    public void addActivity(Activity activity){
        activityRepository.save(activity);
    }

    public void deleteById(String ids){
        String[] id=ids.split(",");
        for (String anId : id) {
            activityRepository.deleteById(anId);
        }
    }
}
