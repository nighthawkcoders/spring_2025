package com.nighthawk.spring_portfolio.mvc.media;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Score class:
 *  - UID assigned to every user
 *  - Person name
 *  - Score as an int
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Scores {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private int uid;
    private String person_name;
    private String className; // CSA, CSP, CSSE, or Other
    private int score;

    @Autowired
    private MediaJpaRepository mediaJpaRepository;

    // Initialize a score object and save it
    public Score init() {
        Score score = new Score("John Doe", 100);
        mediaJpaRepository.save(score);
        return score;
    }
}
