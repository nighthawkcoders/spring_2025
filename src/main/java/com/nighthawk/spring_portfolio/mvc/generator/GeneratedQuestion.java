package com.nighthawk.spring_portfolio.mvc.generator;

// Imports for JPA entity and annotations
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

// Lombok annotations to reduce boilerplate code
import lombok.AllArgsConstructor;  // Generates a constructor with all fields
import lombok.Data;               // Generates getters, setters, toString, equals, and hashCode
import lombok.NoArgsConstructor;  // Generates a no-argument constructor

// Marks this class as a JPA entity
@Entity
@Data  // Lombok: auto-generates getters, setters, and more
@NoArgsConstructor  // Lombok: creates a no-arg constructor
@AllArgsConstructor  // Lombok: creates an all-args constructor
public class GeneratedQuestion {

    // Primary key with auto-incremented ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Field to store the question text
    private String question;
}