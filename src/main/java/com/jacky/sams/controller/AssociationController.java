package com.jacky.sams.controller;

import com.jacky.sams.dao.AssociationDetailRepository;
import com.jacky.sams.entity.Activity;
import com.jacky.sams.entity.AssociationDetail;
import com.jacky.sams.entity.Result;
import com.jacky.sams.entity.SysUser;
import com.jacky.sams.service.ActivityService;
import com.jacky.sams.service.AssociationService;
import com.jacky.sams.util.ImageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.security.pkcs11.P11Util;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;

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
        Result result=new Result();
        result.setResultCode(0);
        if (multipartFile!=null && !multipartFile.isEmpty()) {
            if (!multipartFile.getContentType().contains("image")){
                result.setResultInfo("只能上传图片");
                return result;
            }
            String file_name;
            try {
                file_name = ImageUtil.saveImg(multipartFile, location);
            } catch (IOException e) {
                result.setResultInfo("图片上传失败");
                return result;
            }
            associationDetail.setLogo(file_name);
        }
        associationService.save(associationDetail);
        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setAssociationDetail(associationDetail);
        result.setResultCode(1);
        result.setResultInfo("修改成功");
        return result;
    }

    @RequestMapping("/activity/listPage")
    public String getActivityPage(Model model){
        return "association/activity/list";
    }

    @PostMapping("/activity/list")
    @ResponseBody
    public HashMap<String, Object> getActivity(String name, int pageIndex, int pageSize){
        HashMap<String ,Object> hashMap=new HashMap<>();
        HashMap<String ,Object> paramMap=new HashMap<>();
        paramMap.put("name",name);
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
        activity.setStatus(2);
        activityService.addActivity(activity);
        result.setResultCode(1);
        result.setResultInfo("添加成功");
        return result;
    }

    @PostMapping("/activity/delete")
    @ResponseBody
    public void deleteActivity(String ids){
        activityService.deleteById(ids);
    }
}
