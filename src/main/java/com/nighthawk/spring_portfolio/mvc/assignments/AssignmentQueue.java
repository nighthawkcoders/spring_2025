package com.nighthawk.spring_portfolio.mvc.assignments;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// New Queue class to represent the three lists
@Data
@Entity
public class AssignmentQueue {
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
   
    private List<String> haventGone;
    private List<String> queue;
    private List<String> done;

    public AssignmentQueue() {
        this.haventGone = new ArrayList<>();
        this.queue = new ArrayList<>();
        this.done = new ArrayList<>();
    }

    public void reset() {
        this.haventGone.clear();
        this.queue.clear();
        this.done.clear();
    }
}
