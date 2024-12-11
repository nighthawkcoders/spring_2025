package com.nighthawk.spring_portfolio.mvc.person;


import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PersonSections {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;


   @Column(unique = true)
   private String name;


   @Column(unique = true)
   private String abbreviation;


   @Column(unique = true)
   private int year;


   public PersonSections(String name, String abbreviation, int year) {
       this.name = name;
       this.abbreviation = abbreviation;
       this.year = year;
   }

   public String getName () {
    return this.name;
   }
    // Method to return the current year
    public static int defaultYear() {
        return LocalDate.now().getYear(); // Returns the current year
    }

    public static PersonSections[] initializeSections() {
        return new PersonSections[] {
            new PersonSections("Computer Science A", "CSA", defaultYear()),
            new PersonSections("Computer Science Principles", "CSP", defaultYear()),
            new PersonSections("Engineering Robotics", "Robotics", defaultYear()),
            new PersonSections("Computer Science and Software Engineering", "CSSE", defaultYear())
        };
    }
}
