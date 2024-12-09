package com.nighthawk.spring_portfolio.mvc.synergy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nighthawk.spring_portfolio.mvc.assignments.Assignment;
import com.nighthawk.spring_portfolio.mvc.person.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeJpaRepository extends JpaRepository<Grade, Long> {
    
    Grade findByAssignmentAndStudent(Assignment assignment, Person student);

    List<Grade> findByStudent(Person student);

    List<Grade> findByAssignment(Assignment assignment);

    List<Grade> findByAssignmentId(Long assignmentId);

    Optional<Grade> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    @Query("SELECT DISTINCT g.assignment.id FROM Grade g")
    List<Integer> findAllAssignmentIds();
}
