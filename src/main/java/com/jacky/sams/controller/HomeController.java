package com.jacky.sams.controller;

import com.jacky.sams.entity.AssociationDetail;
import com.jacky.sams.entity.Result;
import com.jacky.sams.service.AssociationService;
import com.jacky.sams.util.ImageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class HomeController {

    /**
     * 在配置文件中配置的文件保存路径
     */
    @Value("${img.location}")
    private String location;

    @Resource
    private AssociationService associationService;

    @RequestMapping(value="/dispatch")
    public String dispatch(){
        //获取当前登录用户的角色信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //根据角色信息跳转页面
        if(auth.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            return "redirect:admin/association/listPage";
        } else if (auth.getAuthorities().toString().equals("[ROLE_ASSOCIATION]")){
            return "redirect:association/activity/listPage";
        } else {
            return "redirect:student/associationList";
        }
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @RequestMapping(value="/header", method = RequestMethod.POST)
    public String header(HttpServletRequest request, HttpServletResponse response) {
        return "admin/common/header";
    }

    @RequestMapping(value="/association/header", method = RequestMethod.POST)
    public String associationHeader(HttpServletRequest request, HttpServletResponse response) {
        return "/association/common/header";
    }

    @RequestMapping(value="/student/header", method = RequestMethod.POST)
    public String studentHeader(HttpServletRequest request, HttpServletResponse response) {
        return "/student/common/header";
    }

    @PostMapping("/apply")
    @ResponseBody
    public Result addAssociation(AssociationDetail associationDetail, @RequestParam(value = "logoPic", required = false) MultipartFile multipartFile) {
        Result result=new Result();
        result.setResultCode(0);
        //查看当前系统是否存在相同名称的社团
        AssociationDetail detail=associationService.findByName(associationDetail.getName());
        if (!StringUtils.isEmpty(detail)){
            result.setResultInfo("当前系统已经存在相同名称的社团，请重新输入！");
            return result;
        }
        //上传图片
        if (multipartFile!=null && !multipartFile.isEmpty()) {
            if (!multipartFile.getContentType().contains("image")){
                result.setResultInfo("只能上传图片");
                return result;
            }
            String file_name;
            try {
                //图片保存地址
                file_name = ImageUtil.saveImg(null,multipartFile, location);
            } catch (IOException e) {
                result.setResultInfo("图片上传失败");
                return result;
            }
            associationDetail.setLogo(file_name);
        }
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //设置申请时间
        associationDetail.setApplyTime(formatter.format(date));
        associationDetail.setPass(2);
        associationService.addAssociation(associationDetail);
        result.setResultCode(1);
        result.setResultInfo("申请成功，请等待管理员审核！");
        return result;
    }
}
