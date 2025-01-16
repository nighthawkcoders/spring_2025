package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for performing CRUD operations on the PersonSections entity.
 * 
 * This interface extends JpaRepository, which provides built-in methods for basic 
 * database operations such as save, find, delete, and more. 
 * Spring Data JPA automatically implements this interface at runtime.
 * 
 * The PersonSectionsJpaRepository provides methods for querying PersonSections data 
 * from the database based on specific attributes, including section names.
 */
public interface PersonSectionsJpaRepository extends JpaRepository<PersonSections, Long> {

    /**
     * Retrieves a PersonSection based on its name.
     * 
     * This method leverages Spring Data JPA's query creation feature, allowing for 
     * the retrieval of a section by its name (e.g., "Computer Science A").
     * 
     * @param name The name of the section (e.g., "Computer Science A").
     * @return The PersonSection entity that matches the given name, or null if no match is found.
     */
    PersonSections findByName(String name);
}
