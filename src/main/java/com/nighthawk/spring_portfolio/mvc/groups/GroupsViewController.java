package com.nighthawk.spring_portfolio.mvc.groups;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mvc/groups")
public class GroupsViewController {
    @GetMapping("/group-tracker")
    public String queueManagement(){
        return "group/group";
    }
}