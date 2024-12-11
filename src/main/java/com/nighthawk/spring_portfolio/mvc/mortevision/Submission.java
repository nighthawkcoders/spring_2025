package com.nighthawk.spring_portfolio.mvc.mortevision;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

import java.util.List;

import org.springframework.expression.spel.ast.Assign;

import com.nighthawk.spring_portfolio.mvc.person.Person;

import groovy.cli.Option;

import java.util.ArrayList;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long SubmissionId;

    private Long userId;

    private String submission_link;

    private String submission_time; // do in this format "09:33:55AM"

    private String assignment_name;

    private long assignment_id;
    
    private String name; 
    
    @ManyToOne
    @JoinColumn(name = "assignment")
    private Assignment assignment;
    
}
