package com.nighthawk.spring_portfolio.mvc.synergy;

import com.nighthawk.spring_portfolio.mvc.assignments.Assignment;
import com.nighthawk.spring_portfolio.mvc.person.Person;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
