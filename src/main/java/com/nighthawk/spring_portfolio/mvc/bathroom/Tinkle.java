package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nighthawk.spring_portfolio.mvc.person.Person;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Tinkle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Sets up a OneToOne join column with the person_id on the person table
    // OnDelete annotation makes it such that the tinkle object will be deleted if the person is deleted
    @OneToOne
    @JoinColumn(name = "person_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Person person;
    private String timeIn;

    // List to store arrays of LocalDateTime pairs for time in and out
    @Column
    @Convert(converter = TimeInOutPairsConverter.class)
    private List<LocalDateTime[]> timeInOutPairs = new ArrayList<>();

    @Column
    private String personName;

    // DateTimeFormatter for parsing and formatting date-time strings
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor for the Tinkle Object
    public Tinkle(Person person, String statsInput) {
        this.person = person;
        this.timeIn = statsInput;
        this.personName = person.getName();
    }

    // Logic to add time in and out pairs
    public void addTimeIn(String timeInOutPairs) {
        if (timeInOutPairs != null && !timeInOutPairs.isEmpty()) {
            if (this.timeInOutPairs == null || this.timeInOutPairs.isEmpty()) {
                this.timeInOutPairs = new ArrayList<>();  
            } else {
                this.timeInOutPairs = new ArrayList<>(this.timeInOutPairs); 
            }
            String[] pairs = timeInOutPairs.split(",");
            for (String pair : pairs) {
                String[] times = pair.split("-");
                if (times.length == 2) {
                    if (times[0].matches("\\d{2}:\\d{2}:\\d{2}")) { // If only time is given
                        times[0] = LocalDateTime.now().toLocalDate() + " " + times[0]; // Prepend current date
                    }
                    if (times[1].matches("\\d{2}:\\d{2}:\\d{2}")) { // If only time is given
                        times[1] = LocalDateTime.now().toLocalDate() + " " + times[1]; // Prepend current date
                    }
                    LocalDateTime parsedTimeIn = LocalDateTime.parse(times[0], formatter);
                    LocalDateTime parsedTimeOut = LocalDateTime.parse(times[1], formatter);
                    this.timeInOutPairs.add(new LocalDateTime[]{parsedTimeIn, parsedTimeOut});
                }
            }
        }
    }

    // Method to add a single time in and out pair
    public void addTimeIn(LocalDateTime timeIn, LocalDateTime timeOut) {
        this.timeInOutPairs.add(new LocalDateTime[]{timeIn, timeOut});
    }

    // Initializing data for the sqlite db
    public String getTimeIn() {
        return this.timeIn;
    }
    
    //Initializing ddata for the sqlite db

    public static Tinkle[] init(Person[] persons) {
        ArrayList<Tinkle> tinkles = new ArrayList<>();

        // Sample data for time in and out pairs
        String[] timeInOutSamples = {
            "2025-01-27 08:45:00--2025-01-27 09:00:00,2025-01-28 10:15:00--2025-01-28 10:30:00", // Entry 1
            "2025-01-27 11:05:00--2025-01-27 11:20:00,2025-01-29 12:00:00--2025-01-29 12:15:00", // Entry 2
            "2025-01-28 09:15:00--2025-01-28 09:30:00,2025-01-30 13:05:00--2025-01-30 13:20:00", // Entry 3
            "2025-01-29 08:50:00--2025-01-29 09:05:00,2025-01-31 14:15:00--2025-01-31 14:30:00", // Entry 4
            "2025-01-27 12:00:00--2025-01-27 12:15:00,2025-01-28 15:20:00--2025-01-28 15:35:00", // Entry 5
            "2025-01-30 10:35:00--2025-01-30 10:50:00,2025-01-31 11:45:00--2025-01-31 12:00:00", // Entry 6
            "2025-01-29 13:15:00--2025-01-29 13:30:00,2025-01-30 14:00:00--2025-01-30 14:15:00"  // Entry 7
        };

        // Assign unique time in and out pairs to each Tinkle entry
        for (int i = 0; i < persons.length; i++) {
            String timeInOut = timeInOutSamples[i % timeInOutSamples.length]; // Reuse samples if more persons exist
            tinkles.add(new Tinkle(persons[i], timeInOut));
        }

        return tinkles.toArray(new Tinkle[0]);
    }
}