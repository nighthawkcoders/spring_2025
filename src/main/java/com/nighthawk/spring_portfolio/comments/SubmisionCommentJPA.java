package com.nighthawk.spring_portfolio.comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; 

public interface SubmisionCommentJPA extends JpaRepository<SubmisionComment, Long> {
    List<SubmisionComment> findByAuthor(String author); // Method to find announcement by author

    List<SubmisionComment> findByAssignment(String assignment);

    List<SubmisionComment> findAllByOrderByTimestampDesc();
}
