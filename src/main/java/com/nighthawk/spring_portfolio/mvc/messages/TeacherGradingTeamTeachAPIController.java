package com.nighthawk.spring_portfolio.mvc.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


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
     * @return a ResponseEntity containing the created submission and HTTP status CREATED
     */
    @PostMapping("/submit")
    public ResponseEntity<TeacherGradingTeamTeach> createAssignment(@RequestBody TeacherGradingTeamTeach teamGrade) {
        teachgergGradingTeamTeachRepo.save(teamGrade);
        return new ResponseEntity<>(teamGrade, HttpStatus.CREATED);
    }

}
