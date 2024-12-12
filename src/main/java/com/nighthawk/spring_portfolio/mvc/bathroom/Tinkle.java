package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.util.ArrayList;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @OneToOne
    @JoinColumn(name = "person_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person person;
    private String timeIn;

    @Column
    private String person_name;
    // @Column

    // private String studentEmail;
    // private String timeIn;
    // private double averageDuration;

    public Tinkle(Person person, String statsInput)
    {
        this.person = person;
        this.timeIn = statsInput;
        this.person_name = person.getName();
    }

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

    // public void addAverageDuration(double averageDuration)
    // {
    //     if (this.averageDuration == 0.0)
    //     {
    //         this.averageDuration = averageDuration;
    //     }
    //     else 
    //     {
    //         this.averageDuration += averageDuration;
    //     }
    // }

    // public static Tinkle[] init()
    // {
    //     ArrayList<Tinkle> users = new ArrayList<>();
    //     users.add(new Tinkle("toby@gmail.com", "", 0.0));
    //     users.add(new Tinkle("lexb@gmail.com", "", 0.0));
    //     users.add(new Tinkle("niko@gmail.com", "", 0.0));
    //     users.add(new Tinkle("madam@gmail.com", "", 0.0));
    //     users.add(new Tinkle("hop@gmail.com", "", 0.0));
    //     users.add(new Tinkle("jm1021@gmail.com", "", 0.0));
    //     users.add(new Tinkle("tarasehdave@gmail.com", "", 0.0));
    //     return users.toArray(new Tinkle[0]);
    // }
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