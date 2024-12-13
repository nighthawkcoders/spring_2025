package com.nighthawk.spring_portfolio.mvc.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/*
 * MediaApiController class:
 *  - Top ranked user is returned by default
 *  - Top ranked user is able to be returned
 */

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "http://localhost:8080")  // Enable CORS for the frontend URL
public class MediaApiController {

    // personJpaRepository used for leaderboard
    @Autowired
    private PersonJpaRepository personJpaRepository;

    @Autowired
    private MediaJpaRepository mediaJpaRepository;

    // Get all analytics records
    // Get all analytics records
    @GetMapping("/")
    public ResponseEntity<List<Scores>> getAllAnalytics() {
        List<Integer> scores = mediaJpaRepository.findAll(); // Fetch leaderboard
        return scores;  // Return list of assignment IDs
    }

    
    @GetMapping("/leaderboard")
    public List<Integer> getLeaderboard() {
        List<Integer> scores = mediaJpaRepository.findAll(); // Fetch leaderboard
        return scores;  // Return list of assignment IDs
    }

    @GetMapping("/score/{person_id}")
    public List<Integer> getLeaderboard() {
        List<Integer> scores = mediaJpaRepository.findAll(); // Fetch leaderboard
        return scores;  // Return list of assignment IDs
    }
}
