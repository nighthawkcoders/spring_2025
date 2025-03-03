package com.nighthawk.spring_portfolio.mvc.rpg.adventureQuestion;



import com.nighthawk.spring_portfolio.mvc.rpg.adventureRubric.AdventureRubric;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AdventureQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = false, nullable = true)
    private String title;

    @Column(unique = false, nullable = false)
    private String content;

    @Column(unique = false, nullable = false)
    private String category;

    @Column(nullable = false)
    private int points;
    
    @ManyToOne
    @JoinColumn(name = "rubric_id", nullable = true)
    private AdventureRubric rubric;
    
    public AdventureQuestion(String title, String content, String category, int points) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.points = points;
  
    }

    public AdventureQuestion(String title, String content, String category, int points, AdventureRubric rubric) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.points = points;
        this.rubric = rubric;
    }

    public static AdventureQuestion createQuestion(String title, String content, int points) {
        AdventureQuestion question = new AdventureQuestion();
        question.setTitle(title);
        question.setContent(content);
        question.setPoints(points);

        return question;
    }

    public static String[][] init() {
        // Create SynergyGrade objects
        return new String[][] {
            {"GitHub Command Quiz", "Which command is used to clone a repository?", "Octocat", "10000", "null"},
            {"GitHub Command Quiz", "Which command is used to add changes to the staging area?", "Octocat", "10000", "null"},
            {"GitHub Command Quiz", "Which command is used to commit changes?", "Octocat", "10000", "null"},
            {"GitHub Command Quiz", "Which command is used to push changes to a remote repository?", "Octocat", "10000", "null"},
            {"GitHub Command Quiz", "Which command is used to pull changes from a remote repository?", "Octocat", "10000", "null"},
            {"GitHub Command Quiz", "Which command is used to check the status of the working directory and staging area?", "Octocat", "10000", "null"},
            {"GitHub Command Quiz", "Which command is used to create a new branch?", "Octocat", "10000", "null"},
            {"GitHub Command Quiz", "Which command is used to switch to a different branch?", "Octocat", "10000", "null"},
            {"GitHub Command Quiz", "Which command is used to merge branches?", "Octocat", "10000", "null"},
            {"GitHub Command Quiz", "Which command is used to view the commit history?", "Octocat", "10000", "null"},

            {"GitHub Command Quiz", "Describe the role of the ls command in a Linux system. Why is it an essential command for managing files and directories?", "Octocat", "10000", "Github ls command"},
            {"GitHub Command Quiz", "Explain the differences between git pull and git fetch. In what scenarios would each command be used?", "Octocat", "10000", "Github git pull and git fetch"},            
            {"GitHub Command Quiz", "What is the purpose of the mkdir command in Linux? Provide an example of its usage in a real-world scenario.", "Octocat", "10000", "Github mkdir"},            
        };
    }
}
