package com.nighthawk.spring_portfolio.mvc.student;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String viewTableDetails(@RequestParam int table, @RequestParam int period, Model model) {
        // Retrieve students by table and period
        List<StudentInfo> students = studentService.findTeam("CSA", 2, period, table);

        // Add attributes to the model
        model.addAttribute("students", students);
        model.addAttribute("progress", calculateProgress(students));
        model.addAttribute("table", table);
        model.addAttribute("period", period);
        // Map<Long, ArrayList<String>> tasks 
        // for (StudentInfo student : students) {

        // }
        // model.addAttribute("tasks", tasks);

        return "student/table-details.html";
    }

    @GetMapping("/studentinfo")
    public String viewStudentInfo(@RequestParam String username, Model model) {
        // Fetch student by username
        StudentInfo student = studentService.findByUsername(username)
                                            .orElseThrow(() -> new RuntimeException("Student not found"));

        // Add student details to the model
        model.addAttribute("student", student);

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
