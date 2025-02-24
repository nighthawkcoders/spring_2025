package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.util.ArrayList;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nighthawk.spring_portfolio.mvc.person.Person;

import jakarta.persistence.Column;
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


    //Sets up a OnetoOne join column with the person_id on the person datable
    // OnDelete annotation makes it such that the tinkle object will be deleted if the 
    @OneToOne
    @JoinColumn(name = "person_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    //TimeIn column is where the entries are stored for when a user checks in and checks out
    private Person person;
    private String timeIn;

    @Column
    private String personName;

    //Constructor for the Tinkle Object
    public Tinkle(Person person, String statsInput)
    {
        this.person = person;
        this.timeIn = statsInput;
        this.personName = person.getName();
    }

    //Logic to add the timeIn value. Example formatting for the timeIn entry: 11:30:12-12:14:10,12:15:14-11:10:9
    public void addTimeIn(String timeIn)
    {
        if (this.timeIn == null || this.timeIn.isEmpty())
        {
            this.timeIn = timeIn;
        }
        else 
        {
            this.timeIn += "," + timeIn;
        }

    }

    //Initializing ddata for the sqlite db
    public static Tinkle[] init(Person[] persons) {
        ArrayList<Tinkle> tinkles = new ArrayList<>();
    
        // Ensure we have enough sample data for unique timeIn values
        String[] timeInSamples = {
            "08:00:00-08:10:00,09:30:00-09:45:00", // Entry 1
            "07:50:00-08:05:00,10:00:00-10:15:00", // Entry 2
            "09:15:00-09:25:00,11:10:00-11:20:00", // Entry 3
            "12:00:00-12:20:00,13:30:00-13:50:00", // Entry 4
            "14:10:00-14:25:00,15:15:00-15:30:00", // Entry 5
            "16:05:00-16:15:00,17:45:00-18:00:00", // Entry 6
            "18:10:00-18:25:00,19:30:00-19:45:00"  // Entry 7
        };
    
        // Assign unique timeIn values to each Tinkle entry
        for (int i = 0; i < persons.length; i++) {
            String timeIn = timeInSamples[i % timeInSamples.length]; // Reuse timeIn samples if more persons exist
            tinkles.add(new Tinkle(persons[i], timeIn));
        }
    
        return tinkles.toArray(new Tinkle[0]);
    }
    
}