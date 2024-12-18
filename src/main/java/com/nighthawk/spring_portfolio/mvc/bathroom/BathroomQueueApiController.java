package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    // DTO class for queue entries
    @Getter
    public static class QueueDto {
        private String teacherEmail; // The email of the teacher associated with the queue
        private String studentName;  // The name of the student to be added or removed
        private String uri;
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addToQueue(@RequestBody QueueDto queueDto) {
        // Check if a queue entry for the teacher already exists
        Optional<BathroomQueue> existingQueue = repository.findByTeacherEmail(queueDto.getTeacherEmail());
        if (existingQueue.isPresent()) {
            // If a queue already exists for the teacher, add the student to it
            existingQueue.get().addStudent(queueDto.getStudentName());
            repository.save(existingQueue.get());
        } else {
            // If no queue exists, create a new one and add the student
            BathroomQueue newQueue = new BathroomQueue(queueDto.getTeacherEmail(), queueDto.getStudentName());
            repository.save(newQueue);
        }
        return new ResponseEntity<>(queueDto.getStudentName() + " was added to " + queueDto.getTeacherEmail(), HttpStatus.CREATED);
    }

    @CrossOrigin(origins = {"http://127.0.0.1:4100", "https://spring2025.nighthawkcodingsociety.com"})
    @DeleteMapping("/remove")
    public ResponseEntity<Object> removeFromQueue(@RequestBody QueueDto queueDto) {
        Optional<BathroomQueue> queueEntry = repository.findByTeacherEmail(queueDto.getTeacherEmail());
        if (queueEntry.isPresent()) {
            BathroomQueue bathroomQueue = queueEntry.get();
            try {
                // Attempt to remove the student from the queue
                bathroomQueue.removeStudent(queueDto.getStudentName());
                repository.save(bathroomQueue);
                return new ResponseEntity<>("Removed " + queueDto.getStudentName() + " from " + queueDto.getTeacherEmail() + "'s queue", HttpStatus.OK);
            } 
            catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
        
        return new ResponseEntity<>("Queue for " + queueDto.getTeacherEmail() + " not found", HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = {"http://127.0.0.1:4100", "https://spring2025.nighthawkcodingsociety.com"})
    @PostMapping("/approve")
    public ResponseEntity<Object> approveStudent(@RequestBody QueueDto queueDto) {
        Optional<BathroomQueue> queueEntry = repository.findByTeacherEmail(queueDto.getTeacherEmail());
        if (queueEntry.isPresent()) {
            BathroomQueue bathroomQueue = queueEntry.get();
            String frontStudent = bathroomQueue.getFrontStudent();
            if (frontStudent != null && frontStudent.equals(queueDto.getStudentName())) {
                // Approve the student and update the queue status
                bathroomQueue.approveStudent();
                repository.save(bathroomQueue);
                return new ResponseEntity<>("Approved " + queueDto.getStudentName(), HttpStatus.OK);
            } 
            else {
                return new ResponseEntity<>("Student is not at the front of the queue", HttpStatus.BAD_REQUEST);
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
                try {
                    String encodedTeacherEmail = URLEncoder.encode(queueDto.getTeacherEmail(), StandardCharsets.UTF_8.toString());
                    String encodedStudentName = URLEncoder.encode(queueDto.getStudentName(), StandardCharsets.UTF_8.toString());
                    String approvalLink = queueDto.getUri()+ "/api/queue/approveLink?teacherEmail=" 
                        + encodedTeacherEmail 
                        + "&studentName=" 
                        + encodedStudentName;

                    EmailDetails emailDetails = new EmailDetails(
                        "dnhsbathroom@gmail.com",
                        "Student Approval Request\n\nPlease click the link below to approve " 
                            + queueDto.getStudentName() + ":\n" 
                            + approvalLink,
                        "Approval Request for Bathroom Access",
                        null
                    );

                    String emailStatus = emailService.sendSimpleMail(emailDetails);
                    if (emailStatus.equals("Email sent successfully!")) {
                        return new ResponseEntity<>("Approval email sent successfully!", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Failed to send approval email: " + emailStatus, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } catch (UnsupportedEncodingException e) {
                    return new ResponseEntity<>("Error encoding URL parameters", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Student is not at the front of the queue", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Queue for " + queueDto.getTeacherEmail() + " not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/approveLink")
    public ResponseEntity<Object> approveStudentViaLink(@RequestParam String teacherEmail, @RequestParam String studentName) {
        Optional<BathroomQueue> queueEntry = repository.findByTeacherEmail(teacherEmail);
        if (queueEntry.isPresent()) {
            BathroomQueue bathroomQueue = queueEntry.get();
            String frontStudent = bathroomQueue.getFrontStudent();
            if (frontStudent != null && frontStudent.equals(studentName)) {
                // Approve the student and update the queue status
                bathroomQueue.approveStudent();
                repository.save(bathroomQueue);
                return new ResponseEntity<>("Approved " + studentName, HttpStatus.OK);
            } 
            else {
                return new ResponseEntity<>("Student is not at the front of the queue", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Queue for " + teacherEmail + " not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BathroomQueue>> getAllQueues() {
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }
   
    @CrossOrigin(origins = {"http://127.0.0.1:4100", "https://spring2025.nighthawkcodingsociety.com"})
    @GetMapping("/getActive")
    public ResponseEntity<Object> getActiveQueues() {
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }
}