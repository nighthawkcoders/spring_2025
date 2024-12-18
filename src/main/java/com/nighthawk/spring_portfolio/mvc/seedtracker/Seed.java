package com.nighthawk.spring_portfolio.mvc.seedtracker;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The @Data annotation from Lombok automatically generates getter and setter methods,
 * along with equals, hashCode, toString, and other utility methods.
 * @AllArgsConstructor generates a constructor with parameters for all fields.
 * @NoArgsConstructor generates a no-argument constructor.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity // Specifies that this class is an entity and is mapped to a database table.
public class Seed {

    @Id // Indicates that this field is the primary key of the entity.
    @GeneratedValue(strategy = GenerationType.AUTO) // Specifies that the ID should be automatically generated.
    private Long id;

    @Column(unique = true) // Specifies that the studentId field should be unique in the database.
    private Long studentId;

    @NotEmpty // Validates that the name field is not empty when creating a Seed instance.
    private String name;

    @NotEmpty // Validates that the comment field is not empty.
    private String comment;

    @DecimalMin("0.0") // Ensures that the grade is at least 0.0.
    private Double grade;

    /**
     * Constructor that initializes the name, comment, and grade fields,
     * without initializing id and studentId fields (which may be auto-generated).
     */
    public Seed(String name, String comment, Double grade) {
        this.name = name;
        this.comment = comment;
        this.grade = grade;
    }

    /**
     * This static method creates and returns a list of Seed objects with predefined data.
     * It's useful for testing or initializing a set of sample data.
     * 
     * @return A list of Seed objects with initial data
     */
    public static List<Seed> createInitialData() {
        List<Seed> seeds = new ArrayList<>();
        seeds.add(new Seed("John Doe", "Good work on the project.", 85.5));
        seeds.add(new Seed("Jane Smith", "Needs improvement in certain areas.", 70.0));
        seeds.add(new Seed("Emily Jones", "Excellent performance!", 95.0));
        seeds.add(new Seed("Chris Brown", "Incomplete assignment.", 50.0));
        seeds.add(new Seed("Alex Green", "Average performance.", 75.0));
        return seeds;
    }

    /**
     * This static method calls createInitialData() to get initial data.
     * It serves as an alias for createInitialData().
     * 
     * @return A list of initial Seed objects
     */
    public static List<Seed> init() {
        return createInitialData();
    }

    /**
     * Main method to run the initialization of data and print each Seed object.
     * This is mainly for testing and demonstration purposes.
     */
    public static void main(String[] args) {
        List<Seed> seeds = init(); // Calls init() to get initial data
        for (Seed seed : seeds) {
            System.out.println(seed); // Prints each Seed object (using toString from @Data)
        }
    }
}

