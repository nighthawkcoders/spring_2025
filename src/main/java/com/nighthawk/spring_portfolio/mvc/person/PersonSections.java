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
 * Represents a section associated with a person, such as a class or a subject.
 * This entity is used to store section-related details in the database and 
 * provides a structure for managing section information like name, abbreviation, 
 * and the year the section is associated with.
 * 
 * A section could represent various educational courses or classes within a system.
 * For example, sections like "Computer Science A" or "Robotics".
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
    private String name; // The full name of the section (e.g., "Computer Science A")

    @Column(unique = true)
    private String abbreviation; // A short abbreviation for the section (e.g., "CSA")

    @Column(unique = true)
    private int year; // The year associated with the section (e.g., 2024)

    /**
     * Constructor for creating a PersonSection with specific name, abbreviation, and year.
     * This constructor can be used when you want to initialize a section with specific details.
     * 
     * @param name The name of the section (e.g., "Computer Science A").
     * @param abbreviation The abbreviation of the section (e.g., "CSA").
     * @param year The year the section is associated with (e.g., 2024).
     */
    public PersonSections(String name, String abbreviation, int year) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.year = year;
    }

    /**
     * Retrieves the name of the section.
     * This method is used to get the full name of the section, which might be used 
     * in various parts of the application where section names are displayed.
     * 
     * @return The name of the section.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the current year. This can be used to set the default year for new sections.
     * It uses the system's current date to return the year dynamically, ensuring the 
     * sections are associated with the correct year when created.
     * 
     * @return The current year as an integer.
     */
    public static int defaultYear() {
        return LocalDate.now().getYear(); // Get the current year using LocalDate
    }

    /**
     * Initializes a set of default sections, typically used for pre-loading data 
     * or creating standard sections automatically. This is useful when you want 
     * to have default sections available in the system without requiring manual entry.
     * 
     * @return An array of default PersonSections, pre-configured with names, abbreviations, and current year.
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
