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

/**
 * Represents a section of a person, such as a class or subject.
 * This entity is used to store section details in the database.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PersonSections {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // Unique identifier for each section

    @Column(unique = true)
    private String name; // Name of the section (e.g., "Computer Science A")

    @Column(unique = true)
    private String abbreviation; // Abbreviation for the section (e.g., "CSA")

    @Column(unique = true)
    private int year; // The year associated with the section (e.g., 2024)

    /**
     * Constructor for creating a PersonSection with specific name, abbreviation, and year.
     * 
     * @param name The name of the section.
     * @param abbreviation The abbreviation of the section.
     * @param year The year the section is associated with.
     */
    public PersonSections(String name, String abbreviation, int year) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.year = year;
    }

    /**
     * Retrieves the name of the section.
     * 
     * @return The name of the section.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the current year. This can be used to set the default year for new sections.
     * 
     * @return The current year as an integer.
     */
    public static int defaultYear() {
        return LocalDate.now().getYear(); // Returns the current year using LocalDate
    }

    /**
     * Initializes a set of default sections, typically used for pre-loading or creating default values.
     * This can be used in cases where you want to create a set of standard sections automatically.
     * 
     * @return An array of default PersonSections.
     */
    public static PersonSections[] initializeSections() {
        return new PersonSections[] {
            new PersonSections("Computer Science A", "CSA", defaultYear()), // Section for Computer Science A
            new PersonSections("Computer Science Principles", "CSP", defaultYear()), // Section for Computer Science Principles
            new PersonSections("Engineering Robotics", "Robotics", defaultYear()), // Section for Robotics
            new PersonSections("Computer Science and Software Engineering", "CSSE", defaultYear()) // Section for CSSE
        };
    }
}
