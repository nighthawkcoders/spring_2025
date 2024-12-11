package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class BathroomQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String teacherEmail;
    private String peopleQueue;
    private int away;

    // Custom constructor
    public BathroomQueue(String teacherEmail, String peopleQueue) {
        this.teacherEmail = teacherEmail;
        this.peopleQueue = peopleQueue;
        this.away = 0;
    }
    
    public void addStudent(String studentName) {
        if (this.peopleQueue == null || this.peopleQueue.isEmpty()) {
            this.peopleQueue = studentName;
        } 
        else {
            this.peopleQueue += "," + studentName;
        }
    }

    public void removeStudent(String studentName) {
        if (this.peopleQueue != null) {
            this.peopleQueue = Arrays.stream(this.peopleQueue.split(","))
                .filter(s -> !s.equals(studentName))
                .collect(Collectors.joining(","));
        }
    }

    public String getFrontStudent() {
        if (this.peopleQueue != null && !this.peopleQueue.isEmpty()) {
            return this.peopleQueue.split(",")[0];
        }
        return null;
    }

    public void approveStudent() {
        if (this.peopleQueue != null && !this.peopleQueue.isEmpty()) {
            if (this.away == 0) {
                // Student is approved to go away
                this.away = 1;
            } else {
                // Student has returned; remove from queue
                String[] students = this.peopleQueue.split(",");
                if (students.length > 1) {
                    this.peopleQueue = String.join(",", Arrays.copyOfRange(students, 1, students.length));
                } else {
                    this.peopleQueue = "";
                }
                this.away = 0;
            }
        } else {
            throw new IllegalStateException("Queue is empty");
        }
    }
    

    public static BathroomQueue[] init() {
        ArrayList<BathroomQueue> queues = new ArrayList<>();
        queues.add(new BathroomQueue("jm1021@gmail.com", ""));
        return queues.toArray(new BathroomQueue[0]);
    }
}