package com.nighthawk.spring_portfolio.mvc.media;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * MediaApiController class:
 *  - Top ranked user is returned by default
 *  - Top ranked user is able to be returned
 */

 @RestController
 @RequestMapping("/api/media")
 @CrossOrigin(origins = "http://localhost:4100") // Enable CORS
 public class MediaApiController {
 
     @Autowired
     private MediaJpaRepository mediaJpaRepository;
 
     // This mapping returns the leaderboard, it is the default. Might need to change this later and add a leaderboard path.
    @GetMapping("/")
    public ResponseEntity<String> getLeaderboard() {
        List<Score> scoresList = mediaJpaRepository.findAllByScoreInc();
        if (scoresList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Get the person at rank 1
        String topRankedPerson = scoresList.get(0).getPersonName();
        return ResponseEntity.ok(topRankedPerson);
    }
 
    // POST to accept score from frontend
    @PostMapping("/score/{personName}/{score}")
    public ResponseEntity<Score> postScore(@PathVariable String personName, @PathVariable int score) {
        Score newScore = new Score(personName, score);
        mediaJpaRepository.save(newScore);
        return new ResponseEntity<>(newScore, HttpStatus.CREATED);
    }
}