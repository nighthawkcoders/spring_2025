package com.nighthawk.spring_portfolio.mvc.messages;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.transaction.Transactional;


/**
 * REST API Controller for managing Teacher Grade submissions.
 * Provides endpoints for CRUD operations on Teacher Grade submissions.
 */
@RestController
@RequestMapping("/api/teachergradingteamteach")
public class TeacherGradingTeamTeachAPIController {

    @Autowired
    private TeacherGradingTeamTeachJPA teachgergGradingTeamTeachRepo;

    /**
     * Create a new Team Grade submit
     * 
     * @param teamGrade the AssignmentSubmission object to be created
     * @return a ResponseEntity containing the TeacherGradingTeamTeach submission and HTTP status CREATED
     */
    @PostMapping("/submit")
    public ResponseEntity<TeacherGradingTeamTeach> createAssignment(@RequestBody TeacherGradingTeamTeach teamGrade) {
        teachgergGradingTeamTeachRepo.save(teamGrade);
        return new ResponseEntity<>(teamGrade, HttpStatus.CREATED);
    }

    /**
     *  get the grade for the team. 
     * 
     * @param TeacherGradingTeamTeachId the ID of the TeacherGradingTeamTeach to be fetch
     * @return a ResponseEntity containing a TeacherGradingTeamTeach or an error 
     */
    @Transactional
    @GetMapping("/teamgrade/{TeacherGradingTeamTeachId}")
    public ResponseEntity<?> getTeacherGradeByAssignment(@PathVariable Long TeacherGradingTeamTeachId) {
        TeacherGradingTeamTeach teamgrade = teachgergGradingTeamTeachRepo.findById(TeacherGradingTeamTeachId).orElse(null);
        if (teamgrade == null) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Assignment not found"), 
                HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<>(teamgrade, HttpStatus.OK);
    }

    
        /**
     * A GET endpoint used for debugging which returns information about every grade
     * @return Information about all the assignments.
     */
    @GetMapping("/debug")
    public ResponseEntity<?> debugTeacherGrades() {
        List<TeacherGradingTeamTeach> TeacherGradingTeamTeachs = teachgergGradingTeamTeachRepo.findAll();
        return new ResponseEntity<>(TeacherGradingTeamTeachs, HttpStatus.OK);
    }
    



}
