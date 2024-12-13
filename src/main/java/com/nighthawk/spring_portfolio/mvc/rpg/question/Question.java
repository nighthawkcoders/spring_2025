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
        questions.add(createQuestion("Unit 1 Popcorn Hack 1", """
                                                              Which of the following is a valid declaration of a variable of type int in Java?
                                                              a) int 123variable;
                                                              b) int variable123;
                                                              c) int variable#123;
                                                              d) int variable 123;""" //
        //
        //
        //
        , 10000));
        questions.add(createQuestion("Unit 1 Popcorn Hack 2", """
                                                              What is the value of the following expression in Java: 5 / 2?
                                                              a) 2.5
                                                              b) 3
                                                              c) 2
                                                              d) 2.0""" //
        //
        //
        //
        , 10000));
        questions.add(createQuestion("Unit 1 Popcorn Hack 3", """
                                                              Which primitive type is used to represent a single character in Java?
                                                              a) char
                                                              b) String
                                                              c) int
                                                              d) byte""" //
        //
        //
        //
        , 10000));
        questions.add(createQuestion("Unit 2 Popcorn Hack 1", """
                                                              Try and create a class in the following code space to represent a dog.
                                                              
                                                              class Dog {
                                                                  ...
                                                              }
                                                              
                                                              public class Main {
                                                                  public static void main(String[] args) {
                                                                      Dog myDog = new Dog("Shelby", "Golden Retriever", 5); // name, breed, age
                                                                      myDog.bark(); // should print "Woof!"
                                                                  }
                                                              }
                                                              """ //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        , 10000));
        questions.add(createQuestion("Unit 2 Popcorn Hack 1", //
        """
        // Uncomment the following method call to run the code and check your answer!
        What will the following code segment print?
        
        public class Concatentations
        {
            public static void main(String[] args)
            {
                String name1 = "Skibidi";
                String name2 = new String("Sigma");
                String name3 = new String(name1);
        
                name1 += "!!"
                String mystery = name1 + name2 + name3
        
                System.out.println(mystery);
            }
        }
        
        // Concatentations.main(null);""" //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        , 10000));

        return questions.toArray(new Question[0]);
    }
}
