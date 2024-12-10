package com.nighthawk.spring_portfolio.mvc.assignments;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    @JsonBackReference  // Add this annotation
    private Assignment assignment;

    private String content;
    private String timestamp;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AssignmentSubmission(Assignment assignment, String content) {
        this.assignment = assignment;
        this.content = content;
        this.timestamp = LocalDateTime.now().format(formatter);
    }

    // submission is a seperate class bc i couldnt integrate into one
}