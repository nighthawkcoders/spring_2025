package com.nighthawk.spring_portfolio.mvc.media;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

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

    // Get Leaderboard
    @GetMapping("/")
    public ResponseEntity<List<Integer>> getLeaderboard() {
        List<Media> mediaList = mediaJpaRepository.findAllByScoreInc();
        List<Integer> scores = mediaList.stream().map(Media::getScore).collect(Collectors.toList()); // Map to scores
        return new ResponseEntity<>(scores, HttpStatus.OK);  // Return scores in increasing order
    }

    
    @GetMapping("/firstplace")
    public List<Integer> getFirstPlace() {
        List<Media> stats = mediaJpaRepository.findFirstPlaceInfo(); // Fetch info on first place
        List<Integer> scores = stats.stream().map(Media::getScore).collect(Collectors.toList()); // Map to scores
        return scores;  // Return scores
    }

    @GetMapping("/score/{person_id}")
    public List<Integer> getScoreByPersonId(@PathVariable Long person_id) {
        List<Media> stats = mediaJpaRepository.findByPersonId(person_id); // Fetch info on specific id
        List<Integer> score = stats.stream().map(Media::getScore).collect(Collectors.toList()); // Map to scores
        return score;  // Return scores
    }
}
