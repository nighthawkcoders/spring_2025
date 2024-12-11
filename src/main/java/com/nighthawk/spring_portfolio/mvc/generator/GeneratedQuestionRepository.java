package com.nighthawk.spring_portfolio.mvc.generator;

// Repository interface for database operations
import org.springframework.data.jpa.repository.JpaRepository;

// Extends JpaRepository to provide CRUD operations for GeneratedQuestion entities
public interface GeneratedQuestionRepository extends JpaRepository<GeneratedQuestion, Long> {
}
