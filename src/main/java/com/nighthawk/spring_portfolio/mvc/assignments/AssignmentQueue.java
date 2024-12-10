package com.nighthawk.spring_portfolio.mvc.assignments;

import java.util.ArrayList;
import java.util.List;

// New Queue class to represent the three lists
public class AssignmentQueue {
    private List<String> working;
    private List<String> waiting;
    private List<String> complete;

    public AssignmentQueue() {
        this.working = new ArrayList<>();
        this.waiting = new ArrayList<>();
        this.complete = new ArrayList<>();
    }

    // Getters and setters for the lists
    public List<String> getWorking() {
        return working;
    }

    public void setWorking(List<String> working) {
        this.working = working;
    }

    public List<String> getWaiting() {
        return waiting;
    }

    public void setWaiting(List<String> waiting) {
        this.waiting = waiting;
    }

    public List<String> getComplete() {
        return complete;
    }

    public void setComplete(List<String> complete) {
        this.complete = complete;
    }

    public void reset() {
        this.working.clear();
        this.waiting.clear();
        this.complete.clear();
    }
}
