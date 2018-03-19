package com.jacky.sams.controller;

import com.jacky.sams.dao.AssociationDetailRepository;
import com.jacky.sams.dao.SysRoleRepository;
import com.jacky.sams.entity.AssociationDetail;
import com.jacky.sams.entity.SysRole;
import com.jacky.sams.entity.SysUser;
import com.jacky.sams.service.AssociationService;
import com.jacky.sams.service.SysUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Resource
    private SysRoleRepository roleRepository;

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
    public ModelAndView list(){
        ModelAndView mav=new ModelAndView();
        mav.setViewName("/admin/associator/list");
        return mav;
    }

    @PostMapping("/asso_manage/listData")
    @ResponseBody
    public HashMap<String, Object> listData(int pageIndex,int pageSize){
        HashMap<String ,Object> hashMap=new HashMap<>();
        Page<AssociationDetail> detailPage=associationService.findAllByPage(pageIndex,pageSize);
        hashMap.put("data",detailPage.getContent());
        hashMap.put("total",detailPage.getTotalElements());
        return hashMap;
    }

    @PostMapping("/asso_manage/delete")
    @ResponseBody
    public void deleteById(String ids){
        associationService.deleteById(ids);
    }

}
