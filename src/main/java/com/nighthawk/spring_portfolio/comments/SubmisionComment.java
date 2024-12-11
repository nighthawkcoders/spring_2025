package com.nighthawk.spring_portfolio.comments;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SubmisionComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(unique = false)
    private String assignment;

    @NotNull
    private String text;

    @NotNull
    private String author;

    private String timestamp;

    

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor with timestamp initialization
    public SubmisionComment(String assignment, String text, String author) {
        this.assignment = assignment;
        this.text = text;
        this.author = author;
        this.timestamp = LocalDateTime.now().format(formatter);
    }

    // Ensure timestamp is initialized if not provided
    public String getTimestamp() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now().format(formatter);
        }
        return this.timestamp;
    }

    public static List<SubmisionComment> createInitialData() {
        List<SubmisionComment> comments = new ArrayList<>();
        comments.add(new SubmisionComment("Reading Log", "This is a test comment", "Kayden"));
        return comments;
    }

    public static List<SubmisionComment> init() {
        return createInitialData();
    }
}
