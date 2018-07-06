package com.jacky.sams.controller;

import com.jacky.sams.entity.*;
import com.jacky.sams.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;

    @Resource
    private SysRoleService roleService;

    @Resource
    private SysUserService userService;

    @Resource
    private AssociationService associationService;

    @Resource
    private ActivityService activityService;

    @PostMapping("/signup")
    @ResponseBody
    public Result signUp(Student student, SysUser user){
        Result result=new Result();
        SysUser oldUser=userService.getUser(user.getUsername());
        //判断系统是否存在相同的账号
        if (oldUser!=null){
            result.setResultCode(0);
            result.setResultInfo("账号已存在，请重新输入！");
            return result;
        }
        //获取学生用户的角色信息
        SysRole role=roleService.getRole("2");
        user.setRole(role);
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //设置注册时间
        user.setSignUpTime(formatter.format(date));
        userService.addUser(user);
        student.setUser(user);
        studentService.addStudent(student);
        result.setResultCode(1);
        return result;
    }

    @RequestMapping(value = "/index")
    public String index(){
        return "student/index";
    }

    @RequestMapping(value = "/detail")
    public String detail(Model model){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student=studentService.findStudentByUserId(user.getId());
        model.addAttribute("student",student);
        return "student/detail";
    }

    @PostMapping(value = "/detail/edit")
    @ResponseBody
    public void editDetail(Student student){
        Student oldStudent=studentService.findOne(student.getId());
        student.setUser(oldStudent.getUser());
        student.setStudentAssociations(oldStudent.getStudentAssociations());
        studentService.addStudent(student);
    }

    @PostMapping(value = "/modifyPwd")
    @ResponseBody
    public void modifyPwd(String pwd, HttpServletRequest request, HttpServletResponse response){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPassword(pwd);
        userService.addUser(user);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }

    @RequestMapping(value = "/associationList")
    public String associationList(Model model){
        List<AssociationDetail> details=associationService.getAssociation();
        model.addAttribute("associations",details);
        return "student/association/list";
    }

    @GetMapping("/association/get/{id}")
    public String getAssociation(Model model,@PathVariable("id") String id){
        AssociationDetail detail=associationService.getAssociation(id);
        model.addAttribute("detail",detail);
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student=studentService.findStudentByUserId(user.getId());
        Set<StudentAssociation> associations=student.getStudentAssociations();
        boolean flag=false;
        for (StudentAssociation studentAssociation:associations){
            if (studentAssociation.getAssociation().getId().equals(detail.getId())){
                flag=true;
                model.addAttribute("studentAssociation",studentAssociation);
                break;
            }
        }
        model.addAttribute("flag",flag);
        return "student/association/detail";
    }

    @PostMapping("/association/enter")
    @ResponseBody
    public void enterAssociation(String id){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //获取执行当前操作的学生
        Student student=studentService.findStudentByUserId(user.getId());
        //确定学生即将加入的社团
        AssociationDetail detail=associationService.getAssociation(id);
        StudentAssociation studentAssociation=new StudentAssociation();
        studentAssociation.setAssociation(detail);
        studentAssociation.setStudent(student);
        studentAssociation.setStatus(2);
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //设置加入社团的申请时间
        studentAssociation.setApplyTime(formatter.format(date));
        detail.getStudentAssociations().add(studentAssociation);
        studentService.addStudent(student);
        associationService.addAssociation(detail);
    }

    @RequestMapping(value = "/activity/listPage")
    public String activityList(Model model,String id){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student=studentService.findStudentByUserId(user.getId());
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("studentId",student.getId());
        paramMap.put("status",1);
        List<AssociationDetail> details=associationService.findAllAssociationByStudent(paramMap);
        model.addAttribute("details",details);
        if (StringUtils.isEmpty(id)) {
            if (details.size()==0){
                List<Activity> startActivities = new ArrayList<>();
                List<Activity> overActivities = new ArrayList<>();
                model.addAttribute("overActivities", overActivities);
                model.addAttribute("startActivities", startActivities);
            }else {
                //加入的社团
                List<String> list = new ArrayList<>();
                for (AssociationDetail detail : details) {
                    list.add(detail.getId());
                }
                //报名中的活动
                HashMap<String, Object> map = new HashMap<>();
                map.put("status", 2);
                map.put("association_ids", list);
                List<Activity> startActivities = activityService.getAllActivity(map);
                model.addAttribute("startActivities", startActivities);
                //已结束的活动
                HashMap<String, Object> mapOver = new HashMap<>();
                mapOver.put("status", 3);
                mapOver.put("association_ids", list);
                List<Activity> overActivities = activityService.getAllActivity(mapOver);
                model.addAttribute("overActivities", overActivities);
                model.addAttribute("id", "");
            }
        }else {
            if ("mine".equals(id)){
                //报名中的活动
                model.addAttribute("id", "mine");
                HashMap<String, Object> map = new HashMap<>();
                map.put("status", 2);
                map.put("stuId",student.getId());
                List<Activity> startActivities = activityService.getAllActivity(map);
                model.addAttribute("startActivities", startActivities);
                //已结束的活动
                HashMap<String, Object> mapOver = new HashMap<>();
                mapOver.put("status", 3);
                mapOver.put("stuId",student.getId());
                List<Activity> overActivities = activityService.getAllActivity(mapOver);
                model.addAttribute("overActivities", overActivities);
            }else {
                //报名中的活动
                model.addAttribute("id", id);
                HashMap<String, Object> map = new HashMap<>();
                map.put("status", 2);
                map.put("association_id", id);
                List<Activity> startActivities = activityService.getAllActivity(map);
                model.addAttribute("startActivities", startActivities);
                //已结束的活动
                HashMap<String, Object> mapOver = new HashMap<>();
                mapOver.put("status", 3);
                mapOver.put("association_id", id);
                List<Activity> overActivities = activityService.getAllActivity(mapOver);
                model.addAttribute("overActivities", overActivities);
            }
        }
        return "student/activity/list";
    }

    @PostMapping("/activity/join")
    @ResponseBody
    public void joinActivity(String id){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //查询当前操作的学生
        Student student=studentService.findStudentByUserId(user.getId());
        //获取选择的活动
        Activity activity=activityService.getActivity(id);
        StudentActivity studentActivity=new StudentActivity();
        studentActivity.setActivity(activity);
        studentActivity.setStudent(student);
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //设置学生报名活动的时间
        studentActivity.setApplyTime(formatter.format(date));
        activity.getStudentActivities().add(studentActivity);
        studentService.addStudent(student);
        activityService.addActivity(activity);
    }

    @GetMapping("/activity/get/{id}")
    public String getActivity(Model model,@PathVariable("id") String id){
        Activity activity=activityService.getActivity(id);
        model.addAttribute("activity",activity);
        Student student=getCurrentStudent();
        Set<StudentActivity> activities=activity.getStudentActivities();
        boolean flag=false;
        for (StudentActivity studentActivity:activities){
            if (studentActivity.getStudent().getId().equals(student.getId())){
                flag=true;
                break;
            }
        }
        model.addAttribute("flag",flag);
        return "/student/activity/detail";
    }

    private Student getCurrentStudent(){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studentService.findStudentByUserId(user.getId());
    }
}
