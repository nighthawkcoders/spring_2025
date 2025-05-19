package com.nighthawk.spring_portfolio.mvc.student;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mvc/student")
public class StudentInfoViewController {

    @Autowired
    private StudentInfo.StudentService studentService;

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

    @GetMapping("/studentinfo")
    public String viewStudentInfo() {
        return "student/student-info.html";
    }

    private int calculateProgress(List<StudentInfo> students) {
        // Example: Calculate progress as a percentage based on completed tasks
        int totalTasks = 0;
        int completedTasks = 0;

        for (StudentInfo student : students) {
            totalTasks += student.getTasks().size();
            completedTasks += student.getCompleted() != null ? student.getCompleted().size() : 0;
        }

        return totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;
    }
}