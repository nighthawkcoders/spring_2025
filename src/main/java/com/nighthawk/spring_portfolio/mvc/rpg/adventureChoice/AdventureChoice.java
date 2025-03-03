package com.nighthawk.spring_portfolio.mvc.rpg.adventureChoice;

import com.nighthawk.spring_portfolio.mvc.rpg.adventureQuestion.AdventureQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AdventureChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AdventureQuestion question;

    @Column(unique = false, nullable = false)
    private String choice;

    @Column(nullable = false)
    private Boolean is_correct;    

    public AdventureChoice (AdventureQuestion question, String choice, Boolean is_correct) {
        this.question = question;
        this.choice = choice;
        this.is_correct = is_correct;
    }    
    
    public static String[][] init() {
        return new String[][] {
            // GitHub Command Quiz
            {"1", "git fork", "false"},
            {"1", "git copy", "false"},
            {"1", "git clone", "true"},
            {"1", "git download", "false"},
            
            {"2", "git commit", "false"},
            {"2", "git push", "false"},
            {"2", "git add", "true"},
            {"2", "git stage", "false"},
            
            {"3", "git add", "false"},
            {"3", "git commit", "true"},
            {"3", "git save", "false"},
            {"3", "git push", "false"},
            
            {"4", "git commit", "false"},
            {"4", "git send", "false"},
            {"4", "git push", "true"},
            {"4", "git upload", "false"},
            
            {"5", "git pull", "true"},
            {"5", "git receive", "false"},
            {"5", "git update", "false"},
            {"5", "git fetch", "false"},
            
            {"6", "git log", "false"},
            {"6", "git check", "false"},
            {"6", "git info", "false"},
            {"6", "git status", "true"},
            
            {"7", "git create-branch", "false"},
            {"7", "git branch", "true"},
            {"7", "git checkout", "false"},
            {"7", "git new-branch", "false"},
            
            {"8", "git checkout", "true"},
            {"8", "git change-branch", "false"},
            {"8", "git switch", "false"},
            {"8", "git branch", "false"},
            
            {"9", "git integrate", "false"},
            {"9", "git join", "false"},
            {"9", "git combine", "false"},
            {"9", "git merge", "true"},
            
            {"10", "git commits", "false"},
            {"10", "git show", "false"},
            {"10", "git history", "false"},
            {"10", "git log", "true"},
            
            {"11", "list", "false"},
            {"11", "dir", "false"},
            {"11", "show", "false"},
            {"11", "ls", "true"},
            
            {"12", "cd", "true"},
            {"12", "changedirectory", "false"},
            {"12", "chdir", "false"},
            {"12", "changedir", "false"},
            
            {"13", "mkdir", "true"},
            {"13", "makedir", "false"},
            {"13", "newdir", "false"},
            {"13", "createdir", "false"},
            
            {"14", "rm", "true"},
            {"14", "delete", "false"},
            {"14", "erase", "false"},
            {"14", "remove", "false"},
            
            {"15", "rmdir", "true"},
            {"15", "deletedir", "false"},
            {"15", "removedir", "false"},
            {"15", "erasedir", "false"},
            
            {"16", "cp", "true"},
            {"16", "xerox", "false"},
            {"16", "duplicate", "false"},
            {"16", "copy", "false"},
            
            {"17", "move", "false"},
            {"17", "mv", "true"},
            {"17", "relocate", "false"},
            {"17", "transfer", "false"},
            
            {"18", "cat", "true"},
            {"18", "display", "false"},
            {"18", "show", "false"},
            {"18", "view", "false"},
            
            {"19", "grep", "true"},
            {"19", "locate", "false"},
            {"19", "find", "false"},
            {"19", "search", "false"},
            
            {"20", "less", "true"},
            {"20", "more", "false"},
            {"20", "cat", "false"},
            {"20", "view", "false"},
            
            {"21", "Shift + Enter", "true"},
            {"21", "Ctrl + Enter", "false"},
            {"21", "Alt + Enter", "false"},
            {"21", "Tab + Enter", "false"},
            
            {"22", "A", "true"},
            {"22", "B", "false"},
            {"22", "C", "false"},
            {"22", "D", "false"},
            
            {"23", "B", "true"},
            {"23", "A", "false"},
            {"23", "C", "false"},
            {"23", "D", "false"},
            
            {"24", "M", "true"},
            {"24", "Y", "false"},
            {"24", "R", "false"},
            {"24", "K", "false"},
            
            {"25", "Y", "true"},
            {"25", "M", "false"},
            {"25", "C", "false"},
            {"25", "D", "false"},
            
            {"26", "D, D", "true"},
            {"26", "X", "false"},
            {"26", "Del", "false"},
            {"26", "Ctrl + D", "false"},
            
            {"27", "Ctrl + S", "true"},
            {"27", "Shift + S", "false"},
            {"27", "Tab + S", "false"},
            {"27", "Alt + S", "false"},
            
            {"28", "0, 0", "true"},
            {"28", "R, R", "false"},
            {"28", "K, K", "false"},
            {"28", "Shift + R", "false"},
            
            {"29", "I, I", "true"},
            {"29", "Ctrl + C", "false"},
            {"29", "Shift + I", "false"},
            {"29", "Alt + I", "false"},
            
            {"30", "L", "true"},
            {"30", "N", "false"},
            {"30", "T", "false"},
            {"30", "G", "false"}
        };
    }
    
}