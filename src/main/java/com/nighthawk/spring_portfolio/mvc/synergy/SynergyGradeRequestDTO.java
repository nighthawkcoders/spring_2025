package com.nighthawk.spring_portfolio.mvc.synergy;

public class SynergyGradeRequestDTO {
    public final Long studentId;
    public final Long assignmentId;
    public final Double gradeSuggestion;
    public final String explanation;

    public SynergyGradeRequestDTO(Long studentId, Long assignmentId, Double gradeSuggestion, String explanation) {
        this.studentId = studentId;
        this.assignmentId = assignmentId;
        this.gradeSuggestion = gradeSuggestion;
        this.explanation = explanation;
    }
}
