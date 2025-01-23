package com.nighthawk.spring_portfolio.mvc.student;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mvc/student")
public class StudentInfoViewController {

    @GetMapping("/tablesper1")
    public String viewTablesPer1() {
        return "student/tables_per1.html";
    }
    
    @GetMapping("/tablesper3")
    public String viewTablesPer3() {
        return "student/tables_per3.html";
    }

    @GetMapping("/tabledetails")
    public String viewTableDetails() {
        return "student/table-details.html";
    }

}
