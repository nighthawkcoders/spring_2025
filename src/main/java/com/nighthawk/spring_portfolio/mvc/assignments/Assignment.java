package com.nighthawk.spring_portfolio.mvc.assignments;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nighthawk.spring_portfolio.mvc.synergy.SynergyGrade;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter

public class Assignment {
    // @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull
    @JsonPropertyOrder({"id", "name", "type", "description", "dueDate", "timestamp", "submissions"})
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=false)
    @NotEmpty
    private String name;

    @NotEmpty
    private String type;

    private String description;

    @NotEmpty
    private String dueDate;

    @NotEmpty
    private String timestamp;

    @OneToMany(mappedBy = "assignment")
    @JsonIgnore
    private List<AssignmentSubmission> submissions;

    @OneToMany(mappedBy="assignment")
    private List<SynergyGrade> grades;

    @NotNull
    private Double points;

    @Convert(converter = QueueConverter.class)
    private Queue assignmentQueue;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void resetQueue() {
        assignmentQueue.reset();
    }

    public void initQueue(List<String> people) {
        assignmentQueue.getHaventGone().addAll(people);
    }

    public void addQueue(String person) {
        assignmentQueue.getHaventGone().remove(person);
        assignmentQueue.getQueue().add(person);
    }

    public void removeQueue(String person) {
        assignmentQueue.getQueue().remove(person);
        assignmentQueue.getHaventGone().add(person);
    }

    public void doneQueue(String person) {
        assignmentQueue.getQueue().remove(person);
        assignmentQueue.getDone().add(person);
    }

    public Assignment(String name, String type, String description, Double points, String dueDate) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.points = points;
        this.dueDate = dueDate; 
        this.timestamp = LocalDateTime.now().format(formatter); // fixed formatting ahhh
        this.assignmentQueue = new Queue();
    }

    public static Assignment[] init() {
        return new Assignment[] {
            new Assignment("Assignment 1", "Class Homework", "Unit 1 Homework", 1.0, "10/25/2024"),
            new Assignment("Sprint 1 Live Review", "Live Review", "The final review for sprint 1", 1.0, "11/2/2024"),
            new Assignment("Seed", "Seed", "The student's seed grade", 1.0, "11/2/2080"),
        };
    }

    @Override
    public String toString(){
        return this.name;
    }
}