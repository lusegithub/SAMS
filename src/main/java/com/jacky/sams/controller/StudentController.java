package com.jacky.sams.controller;

import com.jacky.sams.entity.*;
import com.jacky.sams.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public void signUp(Student student, SysUser user){
        SysRole role=roleService.getRole("2");
        user.setRole(role);
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        user.setSignUpTime(formatter.format(date));
        userService.addUser(user);
        student.setUser(user);
        studentService.addStudent(student);
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
        Student student=studentService.findStudentByUserId(user.getId());
        AssociationDetail detail=associationService.getAssociation(id);
        StudentAssociation studentAssociation=new StudentAssociation();
        studentAssociation.setAssociation(detail);
        studentAssociation.setStudent(student);
        studentAssociation.setStatus(2);
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        studentAssociation.setApplyTime(formatter.format(date));
        detail.getStudentAssociations().add(studentAssociation);
        studentService.addStudent(student);
        associationService.addAssociation(detail);
    }

    @RequestMapping(value = "/activity/listPage")
    public String activityList(Model model){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student=studentService.findStudentByUserId(user.getId());
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("studentId",student.getId());
        paramMap.put("status",1);
        List<AssociationDetail> details=associationService.findAllAssociationByStudent(paramMap);
        model.addAttribute("details",details);
        //加入的社团
        List<String> list=new ArrayList<>();
        for (AssociationDetail detail:details){
            list.add(detail.getId());
        }
        //报名中的活动
        HashMap<String ,Object> map=new HashMap<>();
        map.put("status",2);
        map.put("association_ids",list);
        List<Activity> startActivities=activityService.getAllActivity(map);
        model.addAttribute("startActivities",startActivities);
        //已结束的活动
        HashMap<String ,Object> mapOver=new HashMap<>();
        mapOver.put("status",3);
        mapOver.put("association_ids",list);
        List<Activity> overActivities=activityService.getAllActivity(mapOver);
        model.addAttribute("overActivities",overActivities);
        return "student/activity/list";
    }

    @PostMapping("/activity/join")
    @ResponseBody
    public void joinActivity(String id){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student=studentService.findStudentByUserId(user.getId());
        Activity activity=activityService.getActivity(id);
        StudentActivity studentActivity=new StudentActivity();
        studentActivity.setActivity(activity);
        studentActivity.setStudent(student);
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        studentActivity.setApplyTime(formatter.format(date));
        activity.getStudentActivities().add(studentActivity);
        studentService.addStudent(student);
        activityService.addActivity(activity);
    }

    private Student getCurrentStudent(){
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studentService.findStudentByUserId(user.getId());
    }
}
