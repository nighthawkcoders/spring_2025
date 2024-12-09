package com.nighthawk.spring_portfolio.mvc.assignments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentsApiController {

    @Autowired
    private AssignmentJpaRepository assignmentRepo;

    @Autowired
    private SubmissionJPA submissionRepo;

    @PostMapping("/create") 
    public ResponseEntity<?> createAssignment(
            @RequestParam String name,
            @RequestParam String type,
            @RequestParam String description,
            @RequestParam Double points,
            @RequestParam String dueDate
            ) {
        Assignment newAssignment = new Assignment(name, type, description, points, dueDate);
        Assignment savedAssignment = assignmentRepo.save(newAssignment);
        return new ResponseEntity<>(savedAssignment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllAssignments() {
        List<Assignment> assignments = assignmentRepo.findAll();
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    @PostMapping("/edit/{name}")
    public ResponseEntity<?> editAssignment(
            @PathVariable String name,
            @RequestBody String body) {
        Assignment assignment = assignmentRepo.findByName(name);
        if (assignment != null) {
            assignment.setName(name);
            assignmentRepo.save(assignment);
            return new ResponseEntity<>(assignment, HttpStatus.OK);
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Assignment not found: " + name);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id) {
        Assignment assignment = assignmentRepo.findById(id).orElse(null);
        if (assignment != null) {
            assignmentRepo.delete(assignment);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Assignment deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Assignment not found");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/submit/{assignmentId}")
    public ResponseEntity<?> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam String content) {
        Assignment assignment = assignmentRepo.findById(assignmentId).orElse(null);
        if (assignment != null) {
            Submission submission = new Submission(assignment, content);
            Submission savedSubmission = submissionRepo.save(submission);
            return new ResponseEntity<>(savedSubmission, HttpStatus.CREATED);
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Assignment not found");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<?> getSubmissions(@PathVariable Long assignmentId) {
        List<Submission> submissions = submissionRepo.findByAssignmentId(assignmentId);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @GetMapping("/debug") 
    public ResponseEntity<?> debugAssignments() {
        List<Assignment> assignments = assignmentRepo.findAll();
        List<Map<String, String>> simple = new ArrayList<>();
        for (Assignment a : assignments) {
            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(a.getId()));
            map.put("name", a.getName());
            map.put("description", a.getDescription());
            map.put("dueDate", a.getDueDate());
            map.put("points", String.valueOf(a.getPoints()));
            map.put("type", a.getType());
            simple.add(map);
        }
        return new ResponseEntity<>(simple, HttpStatus.OK);
    }

    @GetMapping("/getQueue/{id}")
    public ResponseEntity<Queue> getQueue(@PathVariable long id) {
        Optional<Assignment> optional = assignmentRepo.findById(id);
        if (optional.isPresent()) {
            Assignment assignment = optional.get();
            
            return new ResponseEntity<>(assignment.getAssignmentQueue(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PutMapping("/initQueue/{id}")
    public ResponseEntity<Assignment> initQueue(@PathVariable long id, @RequestBody List<String> people) {
        Optional<Assignment> optional = assignmentRepo.findById(id);
        if (optional.isPresent()) {
            Assignment assignment = optional.get();
            assignment.initQueue(people);
            assignmentRepo.save(assignment);
            return new ResponseEntity<>(assignment, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/addQueue/{id}")
    public ResponseEntity<Assignment> addQueue(@PathVariable long id, @RequestBody List<String> person) {
        Optional<Assignment> optional = assignmentRepo.findById(id);
        if (optional.isPresent()) {
            Assignment assignment = optional.get();
            assignment.addQueue(person.get(0));
            assignmentRepo.save(assignment);
            return new ResponseEntity<>(assignment, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/removeQueue/{id}")
    public ResponseEntity<Assignment> removeQueue(@PathVariable long id, @RequestBody List<String> person) {
        Optional<Assignment> optional = assignmentRepo.findById(id);
        if (optional.isPresent()) {
            Assignment assignment = optional.get();
            assignment.removeQueue(person.get(0));
            assignmentRepo.save(assignment);
            return new ResponseEntity<>(assignment, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/doneQueue/{id}")
    public ResponseEntity<Assignment> doneQueue(@PathVariable long id, @RequestBody List<String> person) {
        Optional<Assignment> optional = assignmentRepo.findById(id);
        if (optional.isPresent()) {
            Assignment assignment = optional.get();
            assignment.doneQueue(person.get(0));
            assignmentRepo.save(assignment);
            return new ResponseEntity<>(assignment, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/resetQueue/{id}")
    public ResponseEntity<Assignment> resetQueue(@PathVariable long id) {
        Optional<Assignment> optional = assignmentRepo.findById(id);
        if (optional.isPresent()) {
            Assignment assignment = optional.get();
            assignment.resetQueue();
            assignmentRepo.save(assignment);
            return new ResponseEntity<>(assignment, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
