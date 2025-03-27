package com.nighthawk.spring_portfolio.mvc.teamteach;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topicName;
    private String date;

    @ManyToMany
    @JoinTable(
      name = "topic_students", 
      joinColumns = @JoinColumn(name = "topic_id"), 
      inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students;
}
