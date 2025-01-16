package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The PersonRoleJpaRepository interface extends JpaRepository and provides CRUD
 * operations for managing {@link PersonRole} entities in the database.
 * 
 * This repository is used for interacting with the database table that stores
 * roles associated with persons in the system. By extending JpaRepository, 
 * it inherits a set of default CRUD operations (such as save, find, delete, etc.)
 * without the need to define them explicitly.
 * 
 * JpaRepository is a Spring Data interface that provides additional functionality 
 * such as pagination and sorting, on top of the standard CRUD operations. 
 * It is parameterized with the entity type ({@link PersonRole}) and the type of its 
 * primary key (Long).
 */
public interface PersonRoleJpaRepository extends JpaRepository<PersonRole, Long> {

    /**
     * Finds a {@link PersonRole} entity by its unique name.
     * This method is automatically implemented by Spring Data JPA based on the 
     * method name convention. It helps to retrieve a role by its name, ensuring
     * that roles can be looked up efficiently by the role name.
     * 
     * @param name The name of the role (e.g., "ADMIN", "USER").
     * @return A {@link PersonRole} entity with the specified name, or null if no
     *         role with that name exists.
     */
    PersonRole findByName(String name);
}
