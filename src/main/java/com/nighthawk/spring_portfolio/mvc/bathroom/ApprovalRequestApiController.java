package com.nighthawk.spring_portfolio.mvc.bathroom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/queue") // Base API URL
public class ApprovalRequestApiController {

    @Autowired
    private ApprovalRequestJPARepository approvalRepository;

    @PostMapping("/sendApprovalRequest")
    public ResponseEntity<Object> sendApprovalRequest(@RequestBody ApprovalRequest requestDto) {
        // Create new approval request entry in the database
        ApprovalRequest newRequest = new ApprovalRequest(requestDto.getTeacherEmail(), requestDto.getStudentName());
        approvalRepository.save(newRequest);

        return new ResponseEntity<>("Approval request sent!", HttpStatus.CREATED);
    }

    @GetMapping("/pendingRequests/{teacherEmail}")
    public ResponseEntity<List<ApprovalRequest>> getPendingRequests(@PathVariable String teacherEmail) {
        List<ApprovalRequest> pendingRequests = approvalRepository.findByTeacherEmail(teacherEmail);
        return new ResponseEntity<>(pendingRequests, HttpStatus.OK);
    }

    @DeleteMapping("/denyRequest")
    public ResponseEntity<Object> denyRequest(@RequestBody ApprovalRequest requestDto) {
        Optional<ApprovalRequest> request = approvalRepository.findByTeacherEmailAndStudentName(requestDto.getTeacherEmail(), requestDto.getStudentName());
        if (request.isPresent()) {
            approvalRepository.delete(request.get());
            return new ResponseEntity<>("Request denied", HttpStatus.OK);
        }
        return new ResponseEntity<>("Request not found", HttpStatus.NOT_FOUND);
    }
}
