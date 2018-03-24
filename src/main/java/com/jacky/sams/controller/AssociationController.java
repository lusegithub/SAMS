package com.jacky.sams.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/association")
public class AssociationController {

    @RequestMapping(value = "/index")
    public String index(){
        return "association/index";
    }

    @RequestMapping(value = "/detail")
    public String detail(){
        return "association/personal/detail";
    }
}
