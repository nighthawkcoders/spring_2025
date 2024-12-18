package com.nighthawk.spring_portfolio.mvc.media;

import com.nighthawk.spring_portfolio.mvc.person.Person;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    
    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public Score(String person_name, int score) {
        this.person_name = person_name;
        this.score = score;
    }
}