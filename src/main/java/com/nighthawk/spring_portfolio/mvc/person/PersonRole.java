package com.nighthawk.spring_portfolio.mvc.person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * Represents a role that can be assigned to a Person entity.
 * This entity is used for managing user roles and is typically linked to user access control and permissions.
 * Each role is associated with a unique name, which is used to define the role's function or access level within the application.
 * 
 * The PersonRole entity is mapped to a database table, where each role is stored as a row in the table.
 * 
 * Annotations used:
 * - @Entity: Marks this class as a JPA entity that will be mapped to a table in the database.
 * - @Data: Lombok annotation that automatically generates getters, setters, equals(), hashCode(), and toString() methods.
 * - @NoArgsConstructor: Lombok annotation that generates a no-argument constructor.
 * - @AllArgsConstructor: Lombok annotation that generates a constructor with parameters for all fields.
 * - @Id: Marks the id field as the primary key.
 * - @GeneratedValue: Specifies that the id field will be automatically generated with the specified strategy.
 * - @Column(unique = true): Specifies that the "name" field is unique in the database, ensuring no duplicate roles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PersonRole {

    /**
     * The unique identifier for this role.
     * This ID is automatically generated when the role is created.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The name of the role (e.g., "ADMIN", "USER").
     * This field must be unique to ensure that each role name is distinct in the database.
     */
    @Column(unique = true)
    private String name;

    /**
     * Constructor to create a PersonRole with a specific name.
     * This constructor is useful when creating a role without needing to specify an id.
     * 
     * @param name The name of the role (e.g., "ADMIN", "USER").
     */
    public PersonRole(String name) {
        this.name = name;
    }
}
