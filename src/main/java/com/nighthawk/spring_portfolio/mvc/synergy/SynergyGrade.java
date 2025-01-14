package com.nighthawk.spring_portfolio.mvc.synergy;

import com.nighthawk.spring_portfolio.mvc.assignments.Assignment;
import com.nighthawk.spring_portfolio.mvc.assignments.AssignmentJpaRepository;
import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SynergyGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Double grade;

    @ManyToOne
    @JoinColumn(name="assignment_id", nullable=false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name="student_id", nullable=false)
    private Person student;

    public SynergyGrade(Double grade, Assignment assignment, Person student) {
        this.grade = grade;
        this.assignment = assignment;
        this.student = student;
    }

    public static SynergyGrade createFromRequest(SynergyGradeRequest request) {
        return new SynergyGrade(request.getGradeSuggestion(), request.getAssignment(), request.getStudent());
    }

    public static String[][] init() {
        // Create SynergyGrade objects
        return new String[][] {
            {"0.98", "Sprint 1 Live Review", "toby"},
            {"0.85", "Seed", "lex"},
            {"0.92", "Sprint 1 Live Review", "madam"}
        };
    }
}
