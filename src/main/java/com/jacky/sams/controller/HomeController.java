package com.jacky.sams.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HomeController {

    @RequestMapping(value="/dispatch")
    public String dispatch(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            return "redirect:admin/index";
        } else if (auth.getAuthorities().toString().equals("[ROLE_ASSOCIATION]")){
            return "redirect:association/index";
        } else {
            return "redirect:error";
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
        return "/common/header";
    }

    @RequestMapping(value="/association/header", method = RequestMethod.POST)
    public String associationHeader(HttpServletRequest request, HttpServletResponse response) {
        return "/association/common/header";
    }
}
