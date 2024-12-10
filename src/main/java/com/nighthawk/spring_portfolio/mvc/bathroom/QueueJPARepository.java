package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface QueueJPARepository extends JpaRepository<BathroomQueue, Long> {
    Optional<BathroomQueue> findByTeacherName(String teacherName);
    
}
