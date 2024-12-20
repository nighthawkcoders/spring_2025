package com.nighthawk.spring_portfolio.mvc.media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
     public ResponseEntity<List<Map<String, Object>>> getLeaderboard() {
         List<Score> scoresList = mediaJpaRepository.findAllByScoreInc();
         
         if (scoresList.isEmpty()) {
             return ResponseEntity.noContent().build();
         }
     
         // Sort the scores list by score in descending order (so that rank mappins are accurate to highest score)
         scoresList.sort((score1, score2) -> Integer.compare(score2.getScore(), score1.getScore()));
     
         List<Map<String, Object>> leaderboard = new ArrayList<>();
         for (int i = 0; i < scoresList.size(); i++) {
             Score score = scoresList.get(i);
             
             Map<String, Object> entry = new HashMap<>();
             entry.put("rank", i + 1);
             entry.put("username", score.getPersonName());
             entry.put("score", score.getScore());
     
             leaderboard.add(entry);
         }
     
         return ResponseEntity.ok(leaderboard);
     }
 
    
    // POST to accept score from frontend
    @PostMapping("/score/{personName}/{score}")
    public ResponseEntity<Score> postScore(@PathVariable String personName, @PathVariable int score) {
        Score newScore = new Score(personName, score);
        mediaJpaRepository.save(newScore);
        return new ResponseEntity<>(newScore, HttpStatus.CREATED);
    }
}