package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;

@RestController
@RequestMapping("/api/queue")
public class BathroomQueueApiController {

    @Autowired
    private BathroomQueueJPARepository repository;

    @Autowired
    private EmailService emailService;

    @Getter
    public static class QueueDto {
        private String teacherEmail; 
        private String studentName; 
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addToQueue(@RequestBody QueueDto queueDto) {
        Optional<BathroomQueue> existingQueue = repository.findByTeacherEmail(queueDto.getTeacherEmail());
        if (existingQueue.isPresent()) {
            existingQueue.get().addStudent(queueDto.getStudentName());
            repository.save(existingQueue.get());
        } else {
            BathroomQueue newQueue = new BathroomQueue(queueDto.getTeacherEmail(), queueDto.getStudentName());
            repository.save(newQueue);
        }
        return new ResponseEntity<>(queueDto.getStudentName() + " was added to " + queueDto.getTeacherEmail(), HttpStatus.CREATED);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Object> removeFromQueue(@RequestBody QueueDto queueDto) {
        Optional<BathroomQueue> queueEntry = repository.findByTeacherEmail(queueDto.getTeacherEmail());
        if (queueEntry.isPresent()) {
            BathroomQueue bathroomQueue = queueEntry.get();
            try {
                bathroomQueue.removeStudent(queueDto.getStudentName());
                repository.save(bathroomQueue);
                return new ResponseEntity<>("Removed " + queueDto.getStudentName() + " from " + queueDto.getTeacherEmail() + "'s queue", HttpStatus.OK);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("Queue for " + queueDto.getTeacherEmail() + " not found", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/sendApprovalEmail")
    public ResponseEntity<Object> sendApprovalEmail(@RequestBody QueueDto queueDto) {
        Optional<BathroomQueue> queueEntry = repository.findByTeacherEmail(queueDto.getTeacherEmail());
        if (queueEntry.isPresent()) {
            BathroomQueue bathroomQueue = queueEntry.get();
            String frontStudent = bathroomQueue.getFrontStudent();
            if (frontStudent != null && frontStudent.equals(queueDto.getStudentName())) {
                String approvalLink = "http://localhost:8085/api/queue/approveLink?teacherEmail=" 
                    + queueDto.getTeacherEmail() 
                    + "&studentName=" 
                    + queueDto.getStudentName();

                EmailDetails emailDetails = new EmailDetails(
                    "dnhsbathroom@gmail.com",
                    "Student Approval Request\n\nPlease click the link below to approve " 
                        + queueDto.getStudentName() + ":\n" 
                        + approvalLink,
                    "Approval Request for Bathroom Access",
                    null
                );

                emailService.sendSimpleMail(emailDetails);

                return new ResponseEntity<>("Approval email sent successfully!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Student is not at the front of the queue.", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Queue for " + queueDto.getTeacherEmail() + " not found.", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/approveLink")
    public ResponseEntity<Object> approveStudentViaLink(@RequestParam String teacherEmail, @RequestParam String studentName) {
        Optional<BathroomQueue> queueEntry = repository.findByTeacherEmail(teacherEmail);
        if (queueEntry.isPresent()) {
            BathroomQueue bathroomQueue = queueEntry.get();
            String frontStudent = bathroomQueue.getFrontStudent();
            if (frontStudent != null && frontStudent.equals(studentName)) {
                bathroomQueue.approveStudent();
                repository.save(bathroomQueue);
                return new ResponseEntity<>("Approved " + studentName + " for bathroom access.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Student is not at the front of the queue.", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Queue for " + teacherEmail + " not found.", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BathroomQueue>> getAllQueues() {
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }
}
