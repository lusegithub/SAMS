package com.jacky.sams.controller;

import com.jacky.sams.dao.AssociationDetailRepository;
import com.jacky.sams.dao.SysRoleRepository;
import com.jacky.sams.entity.AssociationDetail;
import com.jacky.sams.entity.SysRole;
import com.jacky.sams.entity.SysUser;
import com.jacky.sams.service.AssociationService;
import com.jacky.sams.service.SysRoleService;
import com.jacky.sams.service.SysUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Resource
    private SysRoleService roleService;

    @Resource
    private SysUserService userService;

    @Resource
    private AssociationService associationService;

    @RequestMapping(value = "/index")
    public String index(){
        return "admin/index";
    }

    @RequestMapping("/asso_manage/addAdmin")
    public String addAssoAdmin(){
        return "/admin/associator/add";
    }

    @RequestMapping("/asso_manage/list")
    public String list(Model model){
        List<AssociationDetail> details=associationService.getAssociation();
        model.addAttribute("assos",details);
        return "/admin/associator/list";
    }

    @PostMapping("/asso_manage/listData")
    @ResponseBody
    public HashMap<String, Object> listData(int pageIndex,int pageSize){
        HashMap<String ,Object> hashMap=new HashMap<>();
        Page<SysUser> detailPage=userService.findAllAssoManagerByPage(pageIndex,pageSize);
        hashMap.put("data",detailPage.getContent());
        hashMap.put("total",detailPage.getTotalElements());
        return hashMap;
    }

    @PostMapping("/asso_manage/delete")
    @ResponseBody
    public void deleteById(String ids){
        userService.deleteById(ids);
    }

    @GetMapping("/asso_manage/get")
    @ResponseBody
    public SysUser get(){
//        SysUser user=new SysUser();
//        user.setUsername("test");
//        Page<AssociationDetail> detail=associationService.findAllByPage(1,10);
//        user.setAssociationDetail(detail.getContent().get(0));
//        userService.addUser(user);
        return userService.getUser();
    }

    @PostMapping("/asso_manage/add")
    @ResponseBody
    public void add(SysUser sysUser,String asso_id){
        SysRole role=roleService.getRole("3");
        sysUser.setRole(role);
        AssociationDetail detail=associationService.getAssociation(asso_id);
        sysUser.setAssociationDetail(detail);
        userService.addUser(sysUser);
    }
}
