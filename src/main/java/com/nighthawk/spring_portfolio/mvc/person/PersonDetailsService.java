package com.nighthawk.spring_portfolio.mvc.person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Person entities and integrating with Spring Security.
 * It handles loading user details, assigning roles, and managing passwords.
 */
@Service
@Transactional
public class PersonDetailsService implements UserDetailsService {

    // Injecting dependencies via Spring's Autowiring
    @Autowired
    private PersonJpaRepository personJpaRepository; // Repository for Person entity
    @Autowired
    private PersonRoleJpaRepository personRoleJpaRepository; // Repository for PersonRole entity
    @Autowired
    private PasswordEncoder passwordEncoder; // For password encoding

    /**
     * Loads a user by their unique identifier (ghid).
     * This method is used by Spring Security to authenticate the user.
     *
     * @param ghid The unique identifier for the person (used as username in Spring Security).
     * @return A UserDetails object representing the authenticated user.
     * @throws UsernameNotFoundException if no user is found with the given ghid.
     */
    @Override
    public UserDetails loadUserByUsername(String ghid) throws UsernameNotFoundException {
        Person person = personJpaRepository.findByGhid(ghid); // Fetch user from DB by ghid

        if (person == null) {
            throw new UsernameNotFoundException("User not found with username: " + ghid); // Handle case where user is not found
        }

        // Convert the person's roles into Spring Security's SimpleGrantedAuthority objects
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        person.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName())); // Add each role as authority
        });

        // Create a Spring Security User object with person info and roles
        return new User(person.getGhid(), person.getPassword(), authorities);
    }

    /* Person-related CRUD operations */

    /**
     * Retrieves all Person entities sorted by name in ascending order.
     *
     * @return A list of all Person entities.
     */
    public List<Person> listAll() {
        return personJpaRepository.findAllByOrderByNameAsc(); // Fetch all persons sorted by name
    }

    /**
     * Retrieves Person entities that match the given name or ghid.
     *
     * @param name The name to search for.
     * @param ghid The ghid to search for.
     * @return A list of matching Person entities.
     */
    public List<Person> list(String name, String ghid) {
        return personJpaRepository.findByNameContainingIgnoreCaseOrGhidContainingIgnoreCase(name, ghid); // Search by name or ghid
    }

    /**
     * Retrieves Person entities that contain the given term in their name or ghid.
     * The search is case-insensitive.
     *
     * @param term The search term to look for in name or ghid.
     * @return A list of Person entities containing the search term.
     */
    public List<Person> listLike(String term) {
        return personJpaRepository.findByNameContainingIgnoreCaseOrGhidContainingIgnoreCase(term, term); // Search by term
    }

    /**
     * Retrieves Person entities using a native query with a LIKE condition.
     * The search is case-insensitive and allows partial matching.
     *
     * @param term The search term to use with a LIKE query.
     * @return A list of Person entities matching the term.
     */
    public List<Person> listLikeNative(String term) {
        String likeTerm = String.format("%%%s%%", term); // Format term for LIKE query
        return personJpaRepository.findByLikeTermNative(likeTerm); // Execute the native query
    }

    /**
     * Encodes the person's password and saves the person entity to the database.
     *
     * @param person The person entity to save.
     */
    public void save(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword())); // Encode the password before saving
        personJpaRepository.save(person); // Save person to the database
    }

    public void save(Person person, Boolean samePassword) {
        if(!samePassword){
            person.setPassword(passwordEncoder.encode(person.getPassword())); // Encode the password before saving
        }
        personJpaRepository.save(person); // Save person to the database
    }

    /**
     * Retrieves a Person entity by its ID.
     *
     * @param id The ID of the person to retrieve.
     * @return The Person entity, or null if not found.
     */
    public Person get(long id) {
        return personJpaRepository.findById(id).orElse(null); // Return person if found, else null
    }

    /**
     * Retrieves a Person entity by its ghid (unique identifier).
     *
     * @param ghid The ghid (username) of the person to retrieve.
     * @return The Person entity, or null if not found.
     */
    public Person getByGhid(String ghid) {
        return personJpaRepository.findByGhid(ghid); // Fetch person by ghid
    }

    /**
     * Deletes a Person entity by its ID.
     *
     * @param id The ID of the person to delete.
     */
    public void delete(long id) {
        personJpaRepository.deleteById(id); // Delete person from the database by ID
    }

    /**
     * Sets default passwords and roles for all Person entities.
     * This is used to ensure that users have default credentials.
     *
     * @param password The default password to set for users without one.
     * @param roleName The default role to assign to users without roles.
     */
    public void defaults(String password, String roleName) {
        for (Person person : listAll()) {
            // Set default password if not already set
            if (person.getPassword() == null || person.getPassword().isEmpty() || person.getPassword().isBlank()) {
                person.setPassword(passwordEncoder.encode(password)); // Encode default password
            }
            // Set default role if no role exists
            if (person.getRoles().isEmpty()) {
                PersonRole role = personRoleJpaRepository.findByName(roleName);
                if (role != null) {
                    person.getRoles().add(role); // Assign default role if found
                }
            }
        }
    }

    /**
     * Retrieves all roles available in the system.
     *
     * @return A list of all PersonRole entities.
     */
    public List<PersonRole> listAllRoles() {
        return personRoleJpaRepository.findAll(); // Fetch all roles from the database
    }

    /**
     * Finds a role by its name.
     *
     * @param roleName The name of the role to find.
     * @return The PersonRole entity, or null if not found.
     */
    public PersonRole findRole(String roleName) {
        return personRoleJpaRepository.findByName(roleName); // Fetch role by name
    }

    /**
     * Adds a role to a Person entity identified by ghid.
     *
     * @param ghid The ghid of the person to assign the role to.
     * @param roleName The role to assign to the person.
     */
    public void addRoleToPerson(String ghid, String roleName) {
        Person person = personJpaRepository.findByGhid(ghid); // Find person by ghid
        if (person != null) {
            PersonRole role = personRoleJpaRepository.findByName(roleName); // Find the role by name
            if (role != null) {
                // Add the role to the person if not already assigned
                boolean addRole = true;
                for (PersonRole roleObj : person.getRoles()) {
                    if (roleObj.getName().equals(roleName)) {
                        addRole = false;
                        break;
                    }
                }
                if (addRole) {
                    person.getRoles().add(role); // Add the role if not present
                }
            }
        }
    }

    /**
     * Checks if a Person exists by their ghid.
     *
     * @param ghid The ghid to check.
     * @return true if the person exists, false otherwise.
     */
    public boolean existsByGhid(String ghid) {
        return personJpaRepository.existsByGhid(ghid); // Check existence of person by ghid
    }
}
