package com.nighthawk.spring_portfolio.mvc.synergy;

public class SynergyGradeRequestSelfDTO {
    public final Long assignmentId;
    public final Double gradeSuggestion;
    public final String explanation;

    public SynergyGradeRequestSelfDTO(Long assignmentId, Double gradeSuggestion, String explanation) {
        this.assignmentId = assignmentId;
        this.gradeSuggestion = gradeSuggestion;
        this.explanation = explanation;
    }
}
