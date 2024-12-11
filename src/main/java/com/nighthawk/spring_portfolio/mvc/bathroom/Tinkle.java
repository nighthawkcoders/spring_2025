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
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
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
    public static Tinkle[] init(Person[] persons)
    {
        ArrayList<Tinkle> tinkles = new ArrayList<>();
        for(Person person: persons)
        {
            tinkles.add(new Tinkle(person,"11:12:05-11:13:06,12:15:10-12:19:12"));
        }
        return tinkles.toArray(new Tinkle[0]);
    }
}