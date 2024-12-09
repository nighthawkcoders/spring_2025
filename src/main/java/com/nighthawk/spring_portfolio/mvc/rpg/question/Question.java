package com.nighthawk.spring_portfolio.mvc.rpg.question;



import java.util.ArrayList;

import jakarta.persistence.Column;
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
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = false, nullable = false)
    private String title;

    @Column(unique = false, nullable = false)
    private String content;

    @Column(nullable = false)
    private int points;

    /* 
    @Lob
    @Column(unique = true, nullable = true)    
    private byte[] badge_icon;
    */

    // Constructor
    public Question(String title, String content, int points) {
        this.title = title;
        this.content = content;
        this.points = points;
    }

    /* 
    public static byte[] loadImageAsByteArray(String imagePath) {
        try {
            Path path = Path.of(imagePath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    */
    public static Question createQuestion(String title, String content, int points) {
        Question question = new Question();
        question.setTitle(title);
        question.setContent(content);
        question.setPoints(points);

        return question;
    }

    public static Question[] init() {
        ArrayList<Question> questions = new ArrayList<>();
        
        // byte[] badgeIcon = loadImageAsByteArray("path/to/your/image.png");
        questions.add(createQuestion("Unit 1 Popcorn Hack 1", "What is the output of the following code cell?", 10000));
        questions.add(createQuestion("Unit 1 Popcorn Hack 2", "What is?", 10000));
        questions.add(createQuestion("Unit 1 Popcorn Hack 3", "What is the ", 10000));
        questions.add(createQuestion("Unit 2 Popcorn Hack 1", "What is the output ", 10000));
        questions.add(createQuestion("Unit 2 Popcorn Hack 1", "What is the output of ", 10000));

        return questions.toArray(new Question[0]);
    }
}