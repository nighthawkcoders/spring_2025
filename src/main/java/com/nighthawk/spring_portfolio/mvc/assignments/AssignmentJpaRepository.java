package com.nighthawk.spring_portfolio.mvc.assignments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentJpaRepository extends JpaRepository<Assignment, Long> {
    Assignment findByName(String name);
}