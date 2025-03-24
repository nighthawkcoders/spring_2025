package com.nighthawk.spring_portfolio.mvc.messages;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TeacherGradingTeamTeach {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String team;
    private Double grade;
    private String comment;
    
    public TeacherGradingTeamTeach(String team, Double grade, String comment) {
        this.grade = grade;
        this.comment = comment;
        this.team = team;

    }

    // Getters and Setters (if not using Lombok)
    public Long getId() {
        return id;
    }

    public String getTeam() {
        return team;
    }

    public String getComment() {
        return comment;
    }

    public Double getGrade() {
        return grade;
    }

         /** 
      * This method is an static message to create a Default Team Grade
     * @param team the team
     * @param grade the grade
     * @param comment the comment of the grade
     * @return the TeacherGradingTeamTeach object;
     */
    public static TeacherGradingTeamTeach createTeacherGrade(String team, Double grade, String comment) {
        TeacherGradingTeamTeach teamgrade = new TeacherGradingTeamTeach(team,grade, comment);
        return teamgrade;
    }

      /** Static method to initialize an array list of TeacherGradingTeamTeach objects 
     * @return TeacherGradingTeamTeach[], an array of TeacherGradingTeamTeach objects
     */
    public static TeacherGradingTeamTeach[] init() {
        ArrayList<TeacherGradingTeamTeach> teacherGradingTeamTeachs = new ArrayList<>();
        teacherGradingTeamTeachs.add(createTeacherGrade("TeamA", 97.2, "Great job"));
        teacherGradingTeamTeachs.add(createTeacherGrade("TeamB", 87.2, "Nice job"));
        teacherGradingTeamTeachs.add(createTeacherGrade("TeamC", 77.2, "Good job"));
        return teacherGradingTeamTeachs.toArray(new TeacherGradingTeamTeach[0]);

    }
    
    public static void main(String[] args) {
        // obtain TeacherGradingTeamTeach from initializer
        TeacherGradingTeamTeach teacherGradingTeamTeachs[] = init();

        // iterate using "enhanced for loop"
        for( TeacherGradingTeamTeach teacherGrade : teacherGradingTeamTeachs) {
            System.out.println(teacherGrade);  // print object
        }
    }
}