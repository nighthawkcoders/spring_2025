package com.nighthawk.spring_portfolio.mvc.seedtracker;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SeedJpaRepository extends JpaRepository<Seed, Long> {

    List<Seed> findByStudentId(Long studentId);

    List<Seed> findByCommentContainingIgnoreCase(String keyword);

    List<Seed> findByName(String name);

    List<Seed> findByNameContainingIgnoreCase(String keyword);

    // Custom query to find the maximum studentId value
    @Query("SELECT MAX(s.studentId) FROM Seed s")
    Optional<Long> findMaxStudentId();
}
