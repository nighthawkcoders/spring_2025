package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import jakarta.persistence.PostUpdate;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/approval")
public class ApprovalRequestApiController {

    @Autowired
    private ApprovalRequestJPARepository approvalRepository;

    @Autowired
    private PersonJpaRepository personRepository;

    @Autowired
    private BathroomQueueJPARepository bathroomQueueRepository;

    @PostMapping("/sendApprovalRequest")
    public ResponseEntity<Object> sendApprovalRequest(@RequestBody ApprovalRequest requestDto) {
        System.out.println("Request received: " + requestDto.getStudentName());
    
        ApprovalRequest newRequest = new ApprovalRequest(requestDto.getTeacherEmail(), requestDto.getStudentName());
        approvalRepository.save(newRequest);
    
        return new ResponseEntity<>("Approval request sent successfully!", HttpStatus.CREATED);
    }

    @GetMapping("/pendingRequests")
    public ResponseEntity<List<ApprovalRequest>> getAllPendingRequests() {
        List<ApprovalRequest> pendingRequests = approvalRepository.findAll();
        return new ResponseEntity<>(pendingRequests, HttpStatus.OK);
    }

    // ✅ Approve Request (Removes the request from the database)
    // @PreAuthorize("isAuthenticated()")
    @PostMapping("/approveRequest")
    public ResponseEntity<Object> approveRequest(@RequestBody ApprovalRequest requestDto) {
        System.out.println("🟢 Received Approval Request for: " + requestDto.getStudentName() + " (Teacher: " + requestDto.getTeacherEmail() + ")");

    Optional<ApprovalRequest> request = approvalRepository.findByTeacherEmailAndStudentName(
            requestDto.getTeacherEmail(), requestDto.getStudentName());

    if (request.isPresent()) {
        System.out.println("✅ Request Found in Approval Table: " + requestDto.getStudentName());

        // ✅ Remove from approval table
        approvalRepository.delete(request.get());
        System.out.println("✅ Removed from Approval Table");

        // ✅ Try finding student in Person DB
        Person student = personRepository.findByName(requestDto.getStudentName());

        if (student == null) {
            System.out.println("❌ ERROR: Student not found in Person DB");
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }

        System.out.println("✅ Student Found: " + student.getEmail());

        // ✅ Add student to queue
        BathroomQueue newQueueEntry = new BathroomQueue(requestDto.getTeacherEmail(), requestDto.getStudentName());
        bathroomQueueRepository.save(newQueueEntry);
        System.out.println("✅ Added to Queue: " + student.getEmail());

        return new ResponseEntity<>("Student approved and added to queue", HttpStatus.OK);
    }

    System.out.println("❌ ERROR: Request not found in Approval Table");
    return new ResponseEntity<>("Request not found", HttpStatus.NOT_FOUND); 
    }

    // ✅ Deny Request (Same logic as Approve, just deleting request)
    @DeleteMapping("/denyRequest")
    public ResponseEntity<Object> denyRequest(@RequestBody ApprovalRequest requestDto) {
        Optional<ApprovalRequest> request = approvalRepository.findByTeacherEmailAndStudentName(
                requestDto.getTeacherEmail(), requestDto.getStudentName());

        if (request.isPresent()) {
            approvalRepository.delete(request.get());
            return new ResponseEntity<>("Request denied", HttpStatus.OK);
        }

        return new ResponseEntity<>("Request not found", HttpStatus.NOT_FOUND);
    }
}
