package com.nighthawk.spring_portfolio.mvc.synergy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.nighthawk.spring_portfolio.mvc.assignments.Assignment;
import com.nighthawk.spring_portfolio.mvc.assignments.AssignmentJpaRepository;
import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/api/synergy")
public class SynergyApiController {
    @Autowired
    private SynergyGradeJpaRepository gradeRepository;

    @Autowired
    private SynergyGradeRequestJpaRepository gradeRequestRepository;

    @Autowired
    private AssignmentJpaRepository assignmentRepository;

    @Autowired
    private PersonJpaRepository personRepository;

    /**
     * A POST endpoint to save a single grade to the database.
     * @param grade The parameters for a Grade POJO, passed in as JSON.
     * @return A JSON object confirming that the grade was saved.
     */
    @PostMapping("/grade")
    public ResponseEntity<Map<String, String>> updateGrade(@RequestBody SynergyGrade grade) {
        gradeRepository.save(grade);
        return ResponseEntity.ok(Map.of("message", "Successfully saved this grade."));
    }
    
    /**
     * A POST endpoint to update many grades in bulk.
     * @param grades A formdata which is a map of strings of format grades[ASSIGNMENT_ID][STUDENT_ID] to numerical grades (or empty strings if there is no grade yet)
     * @return A redirect to the gradebook page
     */
    @PostMapping("/grades")
    public ResponseEntity<Map<String, String>> updateAllGrades(@RequestParam Map<String, String> grades) throws ResponseStatusException {
        for (String key : grades.keySet()) {
            String[] ids = key.replace("grades[", "").replace("]", "").split("\\[");
            Long assignmentId = Long.parseLong(ids[0]);
            Long studentId = Long.parseLong(ids[1]);
            String gradeValueStr = grades.get(key);

            if (isNumeric(gradeValueStr)) { // otherwise, we have an empty string so ignore it
                Double gradeValue = Double.parseDouble(gradeValueStr);
                SynergyGrade grade = gradeRepository.findByAssignmentIdAndStudentId(assignmentId, studentId).orElse(null);

                if (grade == null) {
                    Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> 
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid assignment ID passed")
                    );
                    Person student = personRepository.findById(studentId).orElseThrow(() -> 
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid student ID passed")
                    );
    
                    grade = new SynergyGrade();
                    grade.setAssignment(assignment);
                    grade.setStudent(student);
                }
                grade.setGrade(gradeValue);
                gradeRepository.save(grade);
            }
        }

        return ResponseEntity.ok(Map.of("message", "Successfully updated the grades."));
    }
    
    /**
     * A POST endpoint to create a grade request.
     * @param userDetails The information about the logged in user. Automatically passed in by thymeleaf.
     * @param requestData The JSON data passed in, of the format studentId: Long, assignmentId: Long,
     *                    gradeSuggestion: Double, explanation: String
     * @return A JSON object signifying that the request was created.
     */
    @PostMapping("/grades/requests")
    public ResponseEntity<Map<String, String>> createGradeRequest(
        @AuthenticationPrincipal UserDetails userDetails, 
        @RequestBody SynergyGradeRequestDTO requestData
    ) throws ResponseStatusException {
        String email = userDetails.getUsername();
        Person grader = personRepository.findByEmail(email);
        if (grader == null) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "You must be a logged in user to do this"
            );
        }

        Person student = personRepository.findById(requestData.studentId).orElseThrow(() -> 
            new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid student ID passed")
        );
        Assignment assignment = assignmentRepository.findById(requestData.assignmentId).orElseThrow(() -> 
            new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid assignment ID passed")
        );;
        
        SynergyGradeRequest gradeRequest = new SynergyGradeRequest(assignment, student, grader, requestData.explanation, requestData.gradeSuggestion);
        gradeRequestRepository.save(gradeRequest);

        return ResponseEntity.ok(Map.of("message", "Successfully created the grade request."));
    }


    /**
     * A POST endpoint to accept a grade request.
     * @param body The JSON data passed in, of the format requestId: Long
     * @return A JSON object signifying that the request was accepted.
     */
    @PostMapping("/grade/requests/accept")
    public ResponseEntity<Map<String, String>> acceptRequest(@Valid @RequestBody SynergyGradeRequestIdDTO body) throws ResponseStatusException {
        if (body == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request body");
        }

        SynergyGradeRequest request = gradeRequestRepository.findById((long) body.getRequestId()).orElse(null);
        if (request == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Grade request not found"
            );
        }
        else if (request.isAccepted()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Grade request was already accepted before"
            );
        }

        Assignment assignment = request.getAssignment();
        Person student = request.getStudent();
        SynergyGrade grade = gradeRepository.findByAssignmentAndStudent(assignment, request.getStudent());
        if (grade == null) {
            grade = new SynergyGrade();
            grade.setAssignment(assignment);
            grade.setStudent(student);
        }
        grade.setGrade(request.getGradeSuggestion());
        gradeRepository.save(grade);

        request.accept();
        gradeRequestRepository.save(request);

        return ResponseEntity.ok(Map.of("message", "Successfully accepted the grade request."));
    }

    /**
     * Rejects a grade request.
     * @param body The JSON data passed in, of the format requestId: Long
     * @return A JSON object signifying that the request was rejected.
     */
    @PostMapping("/grade/requests/reject")
    public ResponseEntity<Map<String, String>> rejectRequest(@RequestBody SynergyGradeRequestIdDTO body) throws ResponseStatusException {
        if (body == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request body.");
        }
    
        SynergyGradeRequest request = gradeRequestRepository.findById((long) body.getRequestId()).orElse(null);
        if (request == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Grade request not found."
            );
        }
        else if (request.isAccepted()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Grade request was already accepted before."
            );
        }

        request.reject();
        gradeRequestRepository.save(request);

        return ResponseEntity.ok(Map.of("message", "Successfully rejected the grade request."));
    }

    /**
     * Returns whether or not a string is numeric or not (can be a decimal)
     * @param str A string
     * @return A boolean indicating that the string is or is not numeric
     */
    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

}