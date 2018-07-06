package com.jacky.sams.service;

import com.jacky.sams.dao.ActivityRepository;
import com.jacky.sams.dao.impl.CommonDao;
import com.jacky.sams.entity.Activity;
import com.jacky.sams.util.ScheduledUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ActivityService {

    @Resource
    private ActivityRepository activityRepository;

    @Resource
    private CommonDao commonDao;

    @Resource
    private ScheduledUtil scheduledUtil;

    public Page<Activity> findAllActivityByPage(HashMap<String ,Object> hashMap, int pageIndex, int pageSize){
        Specification querySpecifi = (Specification<Activity>) (root, criteriaQuery, criteriaBuilder) -> {
            //构造查询语句
            List<Predicate> predicates = new ArrayList<>();
            //判断是否根据活动名称查询
            if(null != hashMap.get("name") && !hashMap.get("name").equals("")){
                predicates.add(criteriaBuilder.like(root.get("name"), "%"+(String) hashMap.get("name")+"%"));
            }
            //判断是否根据活动状态查询
            if(!StringUtils.isEmpty(hashMap.get("status"))){
                predicates.add(criteriaBuilder.equal(root.get("status").as(Integer.class), hashMap.get("status")));
            }
            //判断是否根据所属社团查询
            if (!StringUtils.isEmpty(hashMap.get("association_id"))){
                predicates.add(criteriaBuilder.equal(root.join("detail").get("id"),hashMap.get("association_id")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //返回搜索结果
        return activityRepository.findAll(querySpecifi,new PageRequest(pageIndex-1,pageSize));
    }

    public void addActivity(Activity activity){
        activityRepository.save(activity);
    }

    public void sendActivity(Activity activity) throws ParseException {
        String overTime=activity.getOverTime();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //获取活动的报名截止时间
        Date over=format.parse(overTime);
        //开启定时任务
        scheduledUtil.addJob(over,new Runnable() {
            @Override
            public void run() {
                //定时修改活动的状态
                changeStatus(activity.getId(),3,2);
            }
        });
        activityRepository.save(activity);
    }

    public void deleteById(String ids){
        String[] id=ids.split(",");
        for (String anId : id) {
            activityRepository.deleteById(anId);
        }
    }

    public void changeStatus(String ids,Integer targetStatus,Integer originalStatus){
        String sql="UPDATE activity a SET a.status=:targetStatus WHERE a.id in (:activityId) and a.status=:originalStatus";
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("targetStatus",targetStatus);
        paramMap.put("originalStatus",originalStatus);
        String[] id=ids.split(",");
        paramMap.put("activityId", Arrays.asList(id));
        commonDao.deleteOrUpDate(sql,paramMap);
    }

    public Activity getActivity(String id){
        return activityRepository.findById(id).orElse(null);
    }

    public List<Activity> getAllActivity(HashMap<String ,Object> hashMap){
        Specification querySpecifi = (Specification<Activity>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(null != hashMap.get("name") && !hashMap.get("name").equals("")){
                predicates.add(criteriaBuilder.like(root.get("name"), "%"+(String) hashMap.get("name")+"%"));
            }
            if(!StringUtils.isEmpty(hashMap.get("status"))){
                predicates.add(criteriaBuilder.equal(root.get("status").as(Integer.class), hashMap.get("status")));
            }
            if(!StringUtils.isEmpty(hashMap.get("association_id"))){
                predicates.add(criteriaBuilder.equal(root.join("detail").get("id"), hashMap.get("association_id")));
            }
            if(!StringUtils.isEmpty(hashMap.get("stuId"))){
                predicates.add(criteriaBuilder.equal(root.join("studentActivities").join("student").get("id"), hashMap.get("stuId")));
            }
            if (!StringUtils.isEmpty(hashMap.get("association_ids"))){
                predicates.add(criteriaBuilder.in(root.join("detail").get("id")).value(hashMap.get("association_ids")));
            }
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sendTime")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return activityRepository.findAll(querySpecifi);
    }
}
