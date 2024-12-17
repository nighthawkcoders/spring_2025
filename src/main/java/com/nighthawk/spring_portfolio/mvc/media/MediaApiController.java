package com.nighthawk.spring_portfolio.mvc.media;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * MediaApiController class:
 *  - Top ranked user is returned by default
 *  - Top ranked user is able to be returned
 */

 @RestController
 @RequestMapping("/api/media")
 @CrossOrigin(origins = "http://localhost:8080") // Enable CORS
 public class MediaApiController {
 
     @Autowired
     private MediaJpaRepository mediaJpaRepository;
 
     // This mapping returns the leaderboard, it is the default. Might need to change this later and add a leaderboard path.
     @GetMapping("/")
     public ResponseEntity<List<Integer>> getLeaderboard() {
         List<Integer> scores = mediaJpaRepository.findAllByScoreInc()
                 .stream()
                 .map(Score::getScore)
                 .collect(Collectors.toList());
         return ResponseEntity.ok(scores); // Formatted response entity
     }
 
     @GetMapping("/firstplace")
     public ResponseEntity<List<Integer>> getFirstPlace() {
         List<Integer> fpInfo = mediaJpaRepository.findFirstPlaceInfo()
                 .stream()
                 .map(Score::getScore)
                 .collect(Collectors.toList());
         return ResponseEntity.ok(fpInfo);
     }
 
     @GetMapping("/score/{personId}")
     public ResponseEntity<List<Integer>> getScoreByPersonId(@PathVariable Long personId) {
         List<Integer> personInfo = mediaJpaRepository.findByPersonId(personId)
                 .stream()
                 .map(Score::getScore)
                 .collect(Collectors.toList());
         return ResponseEntity.ok(personInfo);
     }
 }
