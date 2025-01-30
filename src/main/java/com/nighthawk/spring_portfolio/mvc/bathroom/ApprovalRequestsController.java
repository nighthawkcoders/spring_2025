package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

@Controller  // This is needed to serve Thymeleaf templates
public class ApprovalRequestsController {

    @Autowired
    private ApprovalRequestJPARepository approvalRepository;

    @GetMapping("/admin/approval-requests")
    public String showApprovalRequests(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // ✅ Hardcoded teacher email (replace dynamic lookup)
        String teacherEmail = "jm1021@gmail.com"; 
        System.out.println("🔹 Using Static Teacher Email: " + teacherEmail);

        // ✅ Fetch approval requests for the hardcoded teacher email
        List<ApprovalRequest> pendingRequests = approvalRepository.findByTeacherEmail(teacherEmail);
        System.out.println("✅ Pending Requests: " + pendingRequests.size()); // Debugging

        // ✅ Pass data to Thymeleaf template
        model.addAttribute("pendingRequests", pendingRequests);

        return "approval-requests"; // ✅ Ensure this matches the Thymeleaf template name in /templates/
    }
}
