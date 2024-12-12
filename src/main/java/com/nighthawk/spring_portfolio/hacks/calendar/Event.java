package com.nighthawk.spring_portfolio.hacks.calendar;

import java.time.LocalDate;

public class Event {
    private String title;
    private String description;
    private LocalDate date;

    public Event(String title, String description, LocalDate date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
