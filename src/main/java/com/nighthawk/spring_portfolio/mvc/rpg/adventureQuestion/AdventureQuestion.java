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

            {"Linux Command Quiz", "Which command is used to list files in a directory?", "Tux", "10000", "null"},            
            {"Linux Command Quiz", "Which command is used to change directories?", "Tux", "10000", "null"}, 
            {"Linux Command Quiz", "Which command is used to create a new directory?", "Tux", "10000", "null"}, 
            {"Linux Command Quiz", "Which command is used to remove a file?", "Tux", "10000", "null"}, 
            {"Linux Command Quiz", "Which command is used to remove a directory?", "Tux", "10000", "null"}, 
            {"Linux Command Quiz", "Which command is used to copy files?", "Tux", "10000", "null"}, 
            {"Linux Command Quiz", "Which command is used to move files?", "Tux", "10000", "null"}, 
            {"Linux Command Quiz", "Which command is used to view a file?", "Tux", "10000", "null"}, 
            {"Linux Command Quiz", "Which command is used to search for text in a file?", "Tux", "10000", "null"}, 
            {"Linux Command Quiz", "Which command is used to view the contents of a file?", "Tux", "10000", "null"}, 
            
            {"Jupyter Notebook Command Quiz", "Which shortcut is used to run a cell in Jupyter Notebook?", "Robot", "10000", "null"},            
            {"Jupyter Notebook Command Quiz", "Which shortcut adds a new cell above the current cell?", "Robot", "10000", "null"}, 
            {"Jupyter Notebook Command Quiz", "Which shortcut adds a new cell below the current cell?", "Robot", "10000", "null"}, 
            {"Jupyter Notebook Command Quiz", "Which shortcut changes a cell to Markdown format?", "Robot", "10000", "null"}, 
            {"Jupyter Notebook Command Quiz", "Which shortcut changes a cell to Code format?", "Robot", "10000", "null"}, 
            {"Jupyter Notebook Command Quiz", "Which shortcut deletes the current cell?", "Robot", "10000", "null"}, 
            {"Jupyter Notebook Command Quiz", "Which shortcut saves the current notebook?", "Robot", "10000", "null"}, 
            {"Jupyter Notebook Command Quiz", "Which shortcut restarts the kernel?", "Robot", "10000", "null"}, 
            {"Jupyter Notebook Command Quiz", "Which shortcut interrupts the kernel?", "Robot", "10000", "null"}, 
            {"Jupyter Notebook Command Quiz", "Which shortcut toggles line numbers in a cell?", "Robot", "10000", "null"}, 

            {"Linux Command Quiz", "Describe the role of the ls command in a Linux system. Why is it an essential command for managing files and directories?", "Tux", "10000", "Linux ls command"},      
        };
    }
}
