package com.nighthawk.spring_portfolio.mvc.Grades;

public class UserAssignmentsSummary {
    private String name;
    private String assignmentName;
    private Double grade;

    public UserAssignmentsSummary(String name, String assignmentName, Double grade) {
        this.name = name;
        this.assignmentName = assignmentName;
        this.grade = grade;
    }

    // Getters and setters (or just public fields if you prefer)
    public String getName() {
        return name;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public Double getGrade() {
        return grade;
    }
}
