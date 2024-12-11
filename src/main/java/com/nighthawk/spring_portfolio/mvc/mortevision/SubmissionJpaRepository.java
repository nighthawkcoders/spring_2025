package com.nighthawk.spring_portfolio.mvc.mortevision;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionJpaRepository extends JpaRepository<Submission, Long> {
    // Additional query methods can be added here if needed
    List<Submission> findAllByOrderByNameAsc();
}
