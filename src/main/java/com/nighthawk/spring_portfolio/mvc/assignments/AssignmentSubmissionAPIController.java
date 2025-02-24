package com.nighthawk.spring_portfolio.mvc.assignments;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;
import com.nighthawk.spring_portfolio.mvc.synergy.SynergyGrade;
import com.nighthawk.spring_portfolio.mvc.synergy.SynergyGradeJpaRepository;

import jakarta.transaction.Transactional;

/**
 * REST API Controller for managing assignment submissions.
 * Provides endpoints for CRUD operations on assignment submissions.
 */
@RestController
@RequestMapping("/api/submissions")
public class AssignmentSubmissionAPIController {

    @Autowired
    private AssignmentSubmissionJPA submissionRepo;

    @Autowired
    private AssignmentJpaRepository assignmentRepo;

    @Autowired
    private PersonJpaRepository personRepo;

    @Autowired
    private SynergyGradeJpaRepository gradesRepo;

    /**
     * Get all submissions for a specific student.
     * 
     * @param studentId the ID of the student whose submissions are to be fetched
     * @return a ResponseEntity containing a list of submissions for the given student ID
     */
    @Transactional
    @GetMapping("/getSubmissions/{studentId}")
    public ResponseEntity<List<AssignmentSubmission>> getSubmissions(@PathVariable Long studentId) {
        List<AssignmentSubmission> submissions = submissionRepo.findByStudentId(studentId);
        ResponseEntity<List<AssignmentSubmission>> responseEntity = new ResponseEntity<>(submissions, HttpStatus.OK);
        return responseEntity;
    }

    /**
     * Create a new assignment submission.
     * 
     * @param submission the AssignmentSubmission object to be created
     * @return a ResponseEntity containing the created submission and HTTP status CREATED
     */
    @PostMapping("/Submit")
    public ResponseEntity<AssignmentSubmission> createAssignment(@RequestBody AssignmentSubmission submission) {
        submissionRepo.save(submission);
        return new ResponseEntity<>(submission, HttpStatus.CREATED);
    }

    /**
     * Submit an assignment for a student.
     * 
     * @param assignmentId the ID of the assignment being submitted
     * @param studentId    the ID of the student submitting the assignment
     * @param content      the content of the submission
     * @param comment      any comments related to the submission
     * @return a ResponseEntity containing the created submission or an error if the assignment is not found
     */
    @PostMapping("/submit/{assignmentId}")
    public ResponseEntity<?> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam Long studentId,
            @RequestParam String content,
            @RequestParam String comment,
            @RequestParam Boolean isLate
    ) {
        Assignment assignment = assignmentRepo.findById(assignmentId).orElse(null);
        Person student = personRepo.findById(studentId).orElse(null);
        if (assignment != null) {
            AssignmentSubmission submission = new AssignmentSubmission(assignment, new Person[]{student}, content, comment,isLate);
            AssignmentSubmission savedSubmission = submissionRepo.save(submission);
            return new ResponseEntity<>(savedSubmission, HttpStatus.CREATED);
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Assignment not found");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Grade an existing assignment submission.
     * 
     * @param submissionId the ID of the submission to be graded
     * @param grade        the grade to be assigned to the submission
     * @param feedback     optional feedback for the submission
     * @return a ResponseEntity indicating success or an error if the submission is not found
     */
    @PostMapping("/grade/{submissionId}")
    public ResponseEntity<?> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam Double grade,
            @RequestParam(required = false) String feedback
    ) {
        AssignmentSubmission submission = submissionRepo.findById(submissionId).orElse(null);
        if (submission != null) {
            // we have a correct submission
            submission.setGrade(grade);
            submission.setFeedback(feedback);
            submissionRepo.save(submission);

            for (Person student : submission.getStudents()) {
                SynergyGrade assignedGrade = gradesRepo.findByAssignmentAndStudent(submission.getAssignment(), student);
                if (assignedGrade != null) {
                    // the assignment has a previously assigned grade, so we are just updating it
                    assignedGrade.setGrade(grade);
                }
                else {
                    // assignment is not graded, we must create a new grade
                    SynergyGrade newGrade = new SynergyGrade(grade, submission.getAssignment(), student);
                    gradesRepo.save(newGrade);
                }
            }
            

            return new ResponseEntity<>("It worked", HttpStatus.OK);
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Submission not found");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Get all submissions for a specific assignment.
     * 
     * @param assignmentId the ID of the assignment whose submissions are to be fetched
     * @return a ResponseEntity containing a list of submissions or an error if the assignment is not found
     */
    @Transactional
    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<?> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        Assignment assignment = assignmentRepo.findById(assignmentId).orElse(null);
        if (assignment == null) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Assignment not found"), 
                HttpStatus.NOT_FOUND
            );
        }
        List<AssignmentSubmission> submissions = submissionRepo.findByAssignmentId(assignmentId);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }
}
