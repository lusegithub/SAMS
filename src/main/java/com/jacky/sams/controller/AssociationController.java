package com.jacky.sams.controller;

import com.jacky.sams.dao.AssociationDetailRepository;
import com.jacky.sams.dao.impl.CommonDao;
import com.jacky.sams.entity.*;
import com.jacky.sams.service.ActivityService;
import com.jacky.sams.service.AssociationService;
import com.jacky.sams.service.StudentService;
import com.jacky.sams.service.SysUserService;
import com.jacky.sams.util.ImageUtil;
import com.jacky.sams.vo.StudentActivityVo;
import com.jacky.sams.vo.StudentAssociationVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.security.pkcs11.P11Util;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/association")
public class AssociationController {

    /**
     * 在配置文件中配置的文件保存路径
     */
    @Value("${img.location}")
    private String location;

    @Resource
    private AssociationService associationService;

    @Resource
    private ActivityService activityService;

    @Resource
    private StudentService studentService;

    @Resource
    private SysUserService userService;

    @RequestMapping(value = "/index")
    public String index(){
        return "association/index";
    }

    @RequestMapping(value = "/detail")
    public String detail(Model model){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AssociationDetail detail=user.getAssociationDetail();
        model.addAttribute("detail",detail);
        return "association/personal/detail";
    }

    @RequestMapping(value = "/detail/edit")
    public String editDetail(Model model){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AssociationDetail detail=user.getAssociationDetail();
        model.addAttribute("detail",detail);
        return "association/personal/edit";
    }

    @PostMapping("/detail/edit")
    @ResponseBody
    public Result edit(AssociationDetail associationDetail, @RequestParam(value = "logoPic", required = false) MultipartFile multipartFile){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AssociationDetail detail=associationService.getAssociation(associationDetail.getId());
        Result result=new Result();
        result.setResultCode(0);
        if (multipartFile!=null && !multipartFile.isEmpty()) {
            if (!multipartFile.getContentType().contains("image")){
                result.setResultInfo("只能上传图片");
                return result;
            }
            String file_name;
            try {
                file_name = ImageUtil.saveImg(user,multipartFile, location);
            } catch (IOException e) {
                result.setResultInfo("图片上传失败");
                return result;
            }
            associationDetail.setLogo(file_name);
        }
        associationDetail.setStudentAssociations(detail.getStudentAssociations());
        associationService.save(associationDetail);
        user.setAssociationDetail(associationDetail);
        result.setResultCode(1);
        result.setResultInfo("修改成功");
        return result;
    }

    @PostMapping("/modifyPwd")
    @ResponseBody
    public Result modifyPwd(String password){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Result result=new Result();
        user.setPassword(password);
        userService.addUser(user);
        result.setResultCode(1);
        result.setResultInfo("修改成功,请重新登录！");
        return result;
    }

    @RequestMapping("/activity/listPage")
    public String getActivityPage(Model model){
        return "association/activity/list";
    }

    @PostMapping("/activity/list")
    @ResponseBody
    public HashMap<String, Object> getActivity(String name,Integer status,int pageIndex, int pageSize){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AssociationDetail detail=user.getAssociationDetail();
        HashMap<String ,Object> hashMap=new HashMap<>();
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("association_id",detail.getId());
        paramMap.put("name",name);
        paramMap.put("status",status);
        Page<Activity> detailPage=activityService.findAllActivityByPage(paramMap,pageIndex,pageSize);
        hashMap.put("data",detailPage.getContent());
        hashMap.put("total",detailPage.getTotalElements());
        return hashMap;
    }

    @PostMapping("/activity/add")
    @ResponseBody
    public Result addActivity(Activity activity) {
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AssociationDetail detail=user.getAssociationDetail();
        Result result=new Result();
        activity.setDetail(detail);
        activity.setStatus(4);
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        activity.setApplyTime(formatter.format(date));
        activityService.addActivity(activity);
        result.setResultCode(1);
        result.setResultInfo("添加成功,等待审核");
        return result;
    }

    @GetMapping("/activity/get/{id}")
    public String getActivity(Model model,@PathVariable("id") String id){
        Activity activity=activityService.getActivity(id);
        model.addAttribute("activity",activity);
        return "/association/activity/detail";
    }

    @PostMapping("/activity/delete")
    @ResponseBody
    public void deleteActivity(String ids){
        activityService.deleteById(ids);
    }

    @PostMapping("/activity/send")
    @ResponseBody
    public Result sendActivity(String overTime,String id) throws ParseException {
        Result result=new Result();
        result.setResultCode(0);
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date over=formatter.parse(overTime);
        if (over.before(date)){
            result.setResultInfo("报名结束时间不能早于当前时间！");
            return result;
        }
        Activity activity=activityService.getActivity(id);
        Date start=formatter.parse(activity.getStartTime());
        if (over.after(start)){
            result.setResultInfo("报名结束时间不能晚于活动开始时间！");
            return result;
        }
        activity.setOverTime(overTime);
        activity.setSendTime(formatter.format(date));
        activity.setStatus(2);
        activityService.sendActivity(activity);
        result.setResultCode(1);
        result.setResultInfo("发布成功");
        return result;
    }

    @RequestMapping("/activity/enroll/{id}")
    public String getActivityStudentPage(Model model,@PathVariable("id") String id){
        model.addAttribute("id",id);
        return "association/activity/enroll";
    }

    @PostMapping("/activity/enroll")
    @ResponseBody
    public HashMap<String, Object> getActivityStudent(String activityId,String name,String stuNo, int pageIndex, int pageSize){
        HashMap<String ,Object> hashMap=new HashMap<>();
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("activity_id",activityId);
        if (!StringUtils.isEmpty(name)){
            paramMap.put("name","%"+name+"%");
        }
        if (!StringUtils.isEmpty(stuNo)){
            paramMap.put("stuNo",stuNo);
        }
        Page<StudentActivityVo> students=studentService.findStudentsByActivity(paramMap,pageIndex,pageSize);
        hashMap.put("data",students.getContent());
        hashMap.put("total",students.getTotalElements());
        return hashMap;
    }

    @RequestMapping("/associator/listPage")
    public String getAssociatorPage(Model model){
        return "association/associator/list";
    }

    @PostMapping("/associator/list")
    @ResponseBody
    public HashMap<String, Object> getAssociator(String name,Integer status, int pageIndex, int pageSize){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AssociationDetail detail=user.getAssociationDetail();
        HashMap<String ,Object> hashMap=new HashMap<>();
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("association_id",detail.getId());
        if (!StringUtils.isEmpty(name)){
            paramMap.put("name","%"+name+"%");
        }
        if (!StringUtils.isEmpty(status)){
            paramMap.put("status",status);
        }
        Page<Student> students=studentService.findStudentsByAssociation(paramMap,pageIndex,pageSize);
        hashMap.put("data",students.getContent());
        hashMap.put("total",students.getTotalElements());
        return hashMap;
    }

    @PostMapping("/associator/pass")
    @ResponseBody
    public void pass(String ids,Integer code){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AssociationDetail detail=user.getAssociationDetail();
        if (code == 1) {
            studentService.updateStatus(ids, detail.getId());
        }else {
            studentService.refuse(ids,detail.getId());
        }
    }

    @PostMapping("/student/get")
    @ResponseBody
    public Student getStudent(String id){
        return studentService.findOne(id);
    }
}
