package com.nighthawk.spring_portfolio.mvc.synergy;

public class SynergyGradeDTO {
    private Long id;
    private Double grade;
    private Long assignmentId;
    private Long studentId;

    public SynergyGradeDTO(SynergyGrade grade) {
        this.id = grade.getId();
        this.grade = grade.getGrade();
        this.assignmentId = grade.getAssignment().getId();
        this.studentId = grade.getStudent().getId();
    }

    public Long getId() {
        return this.id;
    }

    public Double getGrade() {
        return this.grade;
    }

    public Long getAssignmentId() {
        return this.assignmentId;
    }

    public Long getStudentId() {
        return this.studentId;
    }
}
