package com.nighthawk.spring_portfolio.mvc.assignments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nighthawk.spring_portfolio.mvc.person.Person;

@Repository
public interface AssignmentJpaRepository extends JpaRepository<Assignment, Long> {
    Assignment findByName(String name);
    List<Assignment> findByAssignedGraders(Person grader);
    // hello this is a test commit
}