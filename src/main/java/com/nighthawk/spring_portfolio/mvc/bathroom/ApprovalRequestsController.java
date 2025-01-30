package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller  // This is needed to serve Thymeleaf templates
public class ApprovalRequestsController {

    @Autowired
    private ApprovalRequestJPARepository approvalRepository;

    @GetMapping("/admin/approval-requests")
    public String showApprovalRequests(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String teacherEmail = userDetails.getUsername();
        List<ApprovalRequest> pendingRequests = approvalRepository.findByTeacherEmail(teacherEmail);
        model.addAttribute("pendingRequests", pendingRequests);

        return "approval-requests"; // Thymeleaf template
    }
}
