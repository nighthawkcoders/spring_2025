package com.nighthawk.spring_portfolio.mvc.rpg.adventureChoice;

import com.nighthawk.spring_portfolio.mvc.rpg.adventureQuestion.AdventureQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AdventureChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AdventureQuestion question;

    @Column(unique = false, nullable = false)
    private String choice_text;

    @Column(unique = false, nullable = false)
    private Boolean is_correct;    

    public AdventureChoice (AdventureQuestion question, String choice_text, Boolean is_correct) {
        this.question = question;
        this.choice_text = choice_text;
        this.is_correct = is_correct;
    }    

}
