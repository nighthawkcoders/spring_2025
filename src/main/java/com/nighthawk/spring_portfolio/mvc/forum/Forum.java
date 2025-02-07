package com.nighthawk.spring_portfolio.mvc.forum;

import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data // Lombok annotation to generate boilerplate code (getters, setters, toString, etc.)
@Entity // JPA annotation to specify that this class is an entity
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor
@AllArgsConstructor // Lombok annotation to generate an all-arguments constructor
@Getter // Lombok annotation to generate getters
@Setter // Lombok annotation to generate setters
public class Forum {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // Primary key for the userStocksTable entity

    @Column(name = "author", nullable=false)
    private String author; // Name of the associated person

    @Column(name = "title", nullable=false)
    private String title; // Title of the post

    @Column(name = "context", nullable=false)
    private String context; // Context of the post

    public Forum(String author, String title, String context) {
        this.author = author;
        this.title = title;
        this.context = context;
    }

    // Static method for creating Forum objects
    public static Forum createQuestion(String author, String title, String context) {
        Forum forum = new Forum();
        forum.setAuthor(author);
        forum.setTitle(title);
        forum.setContext(context);

        return forum;
    }

    // Method to initialize Forum objects using the createQuestion method
    public static Forum[] init() {
        ArrayList<Forum> forums = new ArrayList<>();
        
        // Using the createQuestion method to add forums to the list
        forums.add(createQuestion("Hippo", "Introduction to Java", "Let's discuss the basics of Java programming!"));
        forums.add(createQuestion("Cheetah", "Spring Boot Tips",  "How do you optimize Spring Boot applications for performance?"));
        forums.add(createQuestion("Elephant", "Database Optimization",  "What are the best practices for indexing in SQL databases?"));
        forums.add(createQuestion("Falcon", "Frontend vs Backend",  "Which one do you prefer working on and why?"));
        forums.add(createQuestion("Dolphin", "Machine Learning Trends",  "What are the latest trends in AI and machine learning?"));
    
        return forums.toArray(new Forum[0]);
    }

    public String echoMessage(String message) {
        return message;
    }

    @Override
    public String toString() {
        return String.format("Forum{author='%s', title='%s', context='%s'}", author, title, context);
    }
}
