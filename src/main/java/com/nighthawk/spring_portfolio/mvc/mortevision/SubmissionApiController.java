package com.nighthawk.spring_portfolio.mvc.mortevision;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionApiController {

    @Autowired
    private SubmissionJpaRepository repository_sub;
    // private AssignmentJpaRepository repository_ass;

    // GET queue for a specific assignment
    

    // adding a submission
    @PostMapping("/Submit")
    public ResponseEntity<Submission> createAssignment(@RequestBody Submission submission) {
        System.out.println("here");
        // Optional<Assignment> optional = repository_ass.findByassignmentId(submission.getAssignment_id());
        // Set the associated assignment in the submission
        // submission.setAssignment(optional.get());
        repository_sub.save(submission);
        return new ResponseEntity<>(submission, HttpStatus.CREATED);

    }
    // adding a comment NOT COPLETELY DONE KAYDEN PLS FILL IT OUT IF U WANNA DO THROUGH HERE
    @PutMapping("/comment")
    public ResponseEntity<Submission> comment(@PathVariable long id, @RequestBody String comment) {
        Optional<Submission> optional = repository_sub.findById(id);
        if (optional.isPresent()) {
            Submission submission = optional.get();
            repository_sub.save(submission);
            return new ResponseEntity<>(submission, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


        // GET all submissions
    @GetMapping("/getSubmissions")
    public ResponseEntity<List<Submission>> getPeople() {
        List<Submission> submissions = repository_sub.findAllByOrderByNameAsc();
        ResponseEntity<List<Submission>> responseEntity = new ResponseEntity<>(submissions, HttpStatus.OK);
        return responseEntity;
    }

    // // GET a specific submission by ID
    // @GetMapping("/{id}")
    // public ResponseEntity<Submission> getSubmissionById(@PathVariable long id) {
    //     Optional<Submission> optional = repository_sub.findById(id);
    //     if (optional.isPresent()) {
    //         return new ResponseEntity<>(optional.get(), HttpStatus.OK);
    //     }
    //     return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    // }



    // DELETE a submission
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSubmission(@PathVariable long id) {
        try {
            repository_sub.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    








    

}
