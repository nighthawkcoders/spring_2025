package com.nighthawk.spring_portfolio.mvc.media;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String person_name;
    private int score;

    public Score(String person_name, Long id, int score) {
        this.person_name = person_name;
        this.id = id;
        this.score = score;
    }
}