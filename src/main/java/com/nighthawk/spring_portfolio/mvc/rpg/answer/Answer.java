package com.nighthawk.spring_portfolio.mvc.rpg.answer;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.rpg.question.Question;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    /*   
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    */

    private Long chatScore;

    public Answer (String content, Question question, Person person, Long chatScore) {
        this.content = content;
        this.question = question;
        this.person = person;
        this.chatScore = chatScore;
    }

}