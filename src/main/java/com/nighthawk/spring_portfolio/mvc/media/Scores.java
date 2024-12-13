package com.nighthawk.spring_portfolio.mvc.media;

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


    @Column(unique=true)
    private int uid;
    private String person_name;
    private String className; // CSA, CSP, CSSE, or Other
    private int score;
}
