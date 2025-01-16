package com.nighthawk.spring_portfolio.mvc.person;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * The PersonJpaRepository interface is automatically implemented by Spring Data JPA at runtime.
 * This interface serves as a data access layer for performing CRUD operations on the Person entity.
 * It leverages the Java Persistence API (JPA), specifically Hibernate, to map, store, update, and retrieve 
 * data from relational databases.
 * 
 * By extending JpaRepository, it inherits methods for basic CRUD operations, as well as the ability to define
 * custom queries using Spring Data JPA's naming conventions or JPQL (Java Persistence Query Language).
 * 
 * JpaRepository is a generic interface that requires two parameters:
 * 1. The entity type (Person) to be managed.
 * 2. The type of the entity's primary key (Long in this case).
 * 
 * Commonly used methods provided by JpaRepository include:
 * - save(T entity): Saves the entity to the database. If the entity has an existing ID, it is updated; otherwise, it is inserted.
 * - findById(ID id): Retrieves an entity by its ID.
 * - existsById(ID id): Checks if an entity with the given ID exists in the database.
 * - findAll(): Retrieves all entities of the specified type from the database.
 * - deleteById(ID id): Deletes the entity with the given ID from the database.
 */
public interface PersonJpaRepository extends JpaRepository<Person, Long> {

    /**
     * Retrieves a Person entity by its unique identifier (ghid).
     * The method name follows Spring Data JPA's naming convention to automatically generate the corresponding query.
     * 
     * @param ghid The unique identifier of the person to retrieve.
     * @return The Person entity with the specified ghid, or null if not found.
     */
    Person findByGhid(String ghid);

    /**
     * Retrieves all Person entities from the database, sorted by their name in ascending order.
     * The method uses the `findAllByOrderByNameAsc` naming convention to generate the query.
     * 
     * @return A list of all Person entities, sorted by name.
     */
    List<Person> findAllByOrderByNameAsc();

    /**
     * Retrieves a list of Person entities that match the given name or ghid, ignoring case.
     * This method uses Spring Data JPA's naming conventions to generate the query automatically.
     * 
     * @param name The name to search for (case-insensitive).
     * @param ghid The ghid to search for (case-insensitive).
     * @return A list of Person entities that match the given name or ghid.
     */
    List<Person> findByNameContainingIgnoreCaseOrGhidContainingIgnoreCase(String name, String ghid);

    /**
     * Retrieves a Person entity by both its ghid and password.
     * This method is useful for validating a user's credentials during authentication.
     * 
     * @param ghid The unique identifier (username) of the person.
     * @param password The password of the person.
     * @return The Person entity that matches both the ghid and password, or null if not found.
     */
    Person findByGhidAndPassword(String ghid, String password);

    /**
     * Checks if a Person entity exists with the specified ghid.
     * 
     * @param ghid The unique identifier (username) of the person.
     * @return true if a person with the given ghid exists, false otherwise.
     */
    boolean existsByGhid(String ghid);

    /**
     * Custom query method using the @Query annotation to execute a native SQL query.
     * This query retrieves all Person entities where the name or ghid contains the given search term.
     * The 'nativeQuery = true' indicates that this is a native SQL query, rather than a JPQL query.
     * 
     * @param term The term to search for in the name or ghid fields (case-insensitive).
     * @return A list of Person entities that match the search term.
     */
    @Query(value = "SELECT * FROM Person p WHERE p.name LIKE ?1 or p.ghid LIKE ?1", nativeQuery = true)
    List<Person> findByLikeTermNative(String term);
}
