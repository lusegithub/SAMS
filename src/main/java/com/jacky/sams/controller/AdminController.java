package com.jacky.sams.controller;

import com.jacky.sams.entity.*;
import com.jacky.sams.service.*;
import com.jacky.sams.util.ImageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    /**
     * 在配置文件中配置的文件保存路径
     */
    @Value("${img.location}")
    private String location;

    @Resource
    private SysRoleService roleService;

    @Resource
    private SysUserService userService;

    @Resource
    private AssociationService associationService;

    @Resource
    private StudentService studentService;

    @Resource
    private ActivityService activityService;

    @RequestMapping(value = "/index")
    public String index(){
        return "admin/index";
    }

    @RequestMapping("/asso_manage/list")
    public String list(Model model){
        List<AssociationDetail> details=associationService.getAssociation();
        model.addAttribute("assos",details);
        return "/admin/associator/list";
    }

    @PostMapping("/asso_manage/listData")
    @ResponseBody
    public HashMap<String, Object> listData(String asso_id,String username,int pageIndex,int pageSize){
        HashMap<String ,Object> hashMap=new HashMap<>();
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("username",username);
        paramMap.put("asso_id",asso_id);
        Page<SysUser> detailPage=userService.findAllAssoManagerByPage(paramMap,pageIndex,pageSize);
        hashMap.put("data",detailPage.getContent());
        hashMap.put("total",detailPage.getTotalElements());
        return hashMap;
    }

    @PostMapping("/asso_manage/delete")
    @ResponseBody
    public void deleteById(String ids){
        userService.deleteById(ids);
    }

    @PostMapping("/asso_manage/add")
    @ResponseBody
    public Result add(SysUser sysUser,String asso_id){
        Result result=new Result();
        SysUser user=userService.getUser(sysUser.getUsername());
        if (user!=null){
            result.setResultCode(0);
            result.setResultInfo("账号已存在");
            return result;
        }
        SysRole role=roleService.getRole("3");
        sysUser.setRole(role);
        AssociationDetail detail=associationService.getAssociation(asso_id);
        sysUser.setAssociationDetail(detail);
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sysUser.setSignUpTime(formatter.format(date));
        userService.addUser(sysUser);
        result.setResultCode(1);
        result.setResultInfo("添加成功");
        return result;
    }

    @RequestMapping("/association/listPage")
    public String getAssociationPage(Model model){
        return "/admin/association/list";
    }

    @PostMapping("/association/list")
    @ResponseBody
    public HashMap<String, Object> getAssociations(Integer pass,String name,String category,int pageIndex,int pageSize){
        HashMap<String ,Object> hashMap=new HashMap<>();
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("category",category);
        paramMap.put("name",name);
        paramMap.put("pass",pass);
        Page<AssociationDetail> detailPage=associationService.findAllAssociationByPage(paramMap,pageIndex,pageSize);
        hashMap.put("data",detailPage.getContent());
        hashMap.put("total",detailPage.getTotalElements());
        return hashMap;
    }

    @PostMapping("/association/delete")
    @ResponseBody
    public void deleteAssociation(String ids){
        associationService.deleteById(ids);
    }

    @PostMapping("/association/add")
    @ResponseBody
    public Result addAssociation(AssociationDetail associationDetail, @RequestParam(value = "logoPic", required = false) MultipartFile multipartFile) {
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
        associationDetail.setPass(2);
        associationService.addAssociation(associationDetail);
        result.setResultCode(1);
        result.setResultInfo("添加成功");
        return result;
    }

    @GetMapping("/association/get/{id}")
    public String getAssociation(Model model,@PathVariable("id") String id){
        AssociationDetail detail=associationService.getAssociation(id);
        model.addAttribute("detail",detail);
        return "/admin/association/detail";
    }

    @PostMapping("/association/pass")
    @ResponseBody
    public void pass(String id,Integer passCode){
        associationService.pass(id,passCode);
    }

    @RequestMapping("/student/listPage")
    public String getStudentPage(Model model){
        return "/admin/student/list";
    }

    @PostMapping("/student/list")
    @ResponseBody
    public HashMap<String, Object> getStudents(String stuNo,String name,int pageIndex,int pageSize){
        HashMap<String ,Object> hashMap=new HashMap<>();
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("name",name);
        paramMap.put("stuNo",stuNo);
        Page<Student> students=studentService.findAllStudentByPage(paramMap,pageIndex,pageSize);
        hashMap.put("data",students.getContent());
        hashMap.put("total",students.getTotalElements());
        return hashMap;
    }

    @PostMapping("/student/delete")
    @ResponseBody
    public void deleteStudent(String ids){
        studentService.deleteById(ids);
    }

    @PostMapping("/student/get")
    @ResponseBody
    public Student getStudent(String id){
        return studentService.findOne(id);
    }

    @RequestMapping("/activity/listPage")
    public String getActivityPage(Model model){
        List<AssociationDetail> details=associationService.getAssociation();
        model.addAttribute("assos",details);
        return "/admin/activity/list";
    }

    @PostMapping("/activity/list")
    @ResponseBody
    public HashMap<String, Object> getActivity(String associationId,String name,Integer status,int pageIndex,int pageSize){
        HashMap<String ,Object> hashMap=new HashMap<>();
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("name",name);
        paramMap.put("association_id",associationId);
        paramMap.put("status",status);
        Page<Activity> activities=activityService.findAllActivityByPage(paramMap,pageIndex,pageSize);
        hashMap.put("data",activities.getContent());
        hashMap.put("total",activities.getTotalElements());
        return hashMap;
    }

    @PostMapping("/activity/changeStatus")
    @ResponseBody
    public void changeStatus(String ids,Integer statusCode){
        activityService.changeStatus(ids,statusCode,4);
    }

    @GetMapping("/activity/get/{id}")
    public String getActivity(Model model,@PathVariable("id") String id){
        Activity activity=activityService.getActivity(id);
        model.addAttribute("activity",activity);
        return "/admin/activity/detail";
    }
}
