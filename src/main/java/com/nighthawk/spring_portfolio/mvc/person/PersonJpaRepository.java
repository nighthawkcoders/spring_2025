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
     * Retrieves a Person entity by its unique identifier (uid).
     * The method name follows Spring Data JPA's naming convention to automatically generate the corresponding query.
     * 
     * @param uid The unique identifier of the person to retrieve.
     * @return The Person entity with the specified uid, or null if not found.
     */
    Person findByUid(String uid);

    /**
     * Retrieves all Person entities from the database, sorted by their name in ascending order.
     * The method uses the `findAllByOrderByNameAsc` naming convention to generate the query.
     * 
     * @return A list of all Person entities, sorted by name.
     */
    List<Person> findAllByOrderByNameAsc();

    /**
     * Retrieves a list of Person entities that match the given name or uid, ignoring case.
     * This method uses Spring Data JPA's naming conventions to generate the query automatically.
     * 
     * @param name The name to search for (case-insensitive).
     * @param uid The uid to search for (case-insensitive).
     * @return A list of Person entities that match the given name or uid.
     */
    List<Person> findByNameContainingIgnoreCaseOrUidContainingIgnoreCase(String name, String uid);

    /**
     * Retrieves a Person entity by both its uid and password.
     * This method is useful for validating a user's credentials during authentication.
     * 
     * @param uid The unique identifier (username) of the person.
     * @param password The password of the person.
     * @return The Person entity that matches both the uid and password, or null if not found.
     */
    Person findByUidAndPassword(String uid, String password);

    /**
     * Checks if a Person entity exists with the specified uid.
     * 
     * @param uid The unique identifier (username) of the person.
     * @return true if a person with the given uid exists, false otherwise.
     */
    boolean existsByUid(String uid);

    /**
     * Custom query method using the @Query annotation to execute a native SQL query.
     * This query retrieves all Person entities where the name or uid contains the given search term.
     * The 'nativeQuery = true' indicates that this is a native SQL query, rather than a JPQL query.
     * 
     * @param term The term to search for in the name or uid fields (case-insensitive).
     * @return A list of Person entities that match the search term.
     */
    @Query(value = "SELECT * FROM Person p WHERE p.name LIKE ?1 or p.uid LIKE ?1", nativeQuery = true)
    List<Person> findByLikeTermNative(String term);
}
