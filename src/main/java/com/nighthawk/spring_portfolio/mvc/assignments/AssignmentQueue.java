package com.nighthawk.spring_portfolio.mvc.assignments;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import lombok.Data;

// New Queue class to represent the three lists
@Data
@Entity
public class AssignmentQueue {
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
