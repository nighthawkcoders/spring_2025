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
 * Service class that integrates with Spring Security to manage user authentication and roles.
 * It handles CRUD operations for the `Person` entity, password encoding, role assignments,
 * and ensuring users have valid default credentials.
 */
@Service
@Transactional
public class PersonDetailsService implements UserDetailsService {

    // Injecting dependencies via Spring's Autowiring
    @Autowired
    private PersonJpaRepository personJpaRepository; // Repository for accessing and managing Person data
    @Autowired
    private PersonRoleJpaRepository personRoleJpaRepository; // Repository for managing user roles
    @Autowired
    private PasswordEncoder passwordEncoder; // For securely encoding user passwords

    /**
     * Loads user details for authentication using Spring Security.
     * 
     * @param ghid The unique identifier (username) of the person to load.
     * @return A UserDetails object representing the authenticated user.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String ghid) throws UsernameNotFoundException {
        // Fetch the person entity from the database by ghid (used as the username)
        Person person = personJpaRepository.findByGhid(ghid); 

        if (person == null) {
            // If no person is found, throw a UsernameNotFoundException (this is used by Spring Security)
            throw new UsernameNotFoundException("User not found with username: " + ghid);
        }

        // Convert the person's roles into Spring Security authorities
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        person.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName())); // Add each role as an authority
        });

        // Return a Spring Security User object containing the person's ghid, encoded password, and authorities
        return new User(person.getGhid(), person.getPassword(), authorities);
    }

    /* CRUD Operations for managing Person entities */

    /**
     * Retrieves all Person entities, sorted by name in ascending order.
     * 
     * @return A list of all Person entities.
     */
    public List<Person> listAll() {
        return personJpaRepository.findAllByOrderByNameAsc(); // Fetch all persons, ordered by name
    }

    /**
     * Retrieves a list of Person entities that match the given name or ghid.
     * 
     * @param name The name to search for.
     * @param ghid The ghid (unique identifier) to search for.
     * @return A list of matching Person entities.
     */
    public List<Person> list(String name, String ghid) {
        return personJpaRepository.findByNameContainingIgnoreCaseOrGhidContainingIgnoreCase(name, ghid); // Search by name or ghid
    }

    /**
     * Searches for Person entities that contain the given term in their name or ghid.
     * The search is case-insensitive.
     * 
     * @param term The search term to look for.
     * @return A list of matching Person entities.
     */
    public List<Person> listLike(String term) {
        return personJpaRepository.findByNameContainingIgnoreCaseOrGhidContainingIgnoreCase(term, term); // Perform a case-insensitive search by term
    }

    /**
     * Retrieves Person entities using a native SQL query with a LIKE condition.
     * The search is case-insensitive and allows partial matches.
     * 
     * @param term The search term for the LIKE query.
     * @return A list of matching Person entities.
     */
    public List<Person> listLikeNative(String term) {
        String likeTerm = String.format("%%%s%%", term); // Format the term for a LIKE query
        return personJpaRepository.findByLikeTermNative(likeTerm); // Execute the native query
    }

    /**
     * Encodes the person's password and saves the person entity to the database.
     * 
     * @param person The person entity to save.
     */
    public void save(Person person) {
        // Encode the password before saving it to the database
        person.setPassword(passwordEncoder.encode(person.getPassword())); 
        personJpaRepository.save(person); // Save the person to the database
    }

    /**
     * Saves the person entity, with an optional check for whether the password should be re-encoded.
     * 
     * @param person The person entity to save.
     * @param samePassword A flag indicating whether the password remains unchanged (if true).
     */
    public void save(Person person, Boolean samePassword) {
        if (!samePassword) {
            // Encode the password only if it's not the same as before
            person.setPassword(passwordEncoder.encode(person.getPassword()));
        }
        personJpaRepository.save(person); // Save the person to the database
    }

    /**
     * Retrieves a Person entity by its unique identifier (ID).
     * 
     * @param id The ID of the person to retrieve.
     * @return The Person entity, or null if not found.
     */
    public Person get(long id) {
        return personJpaRepository.findById(id).orElse(null); // Return the person if found, otherwise null
    }

    /**
     * Retrieves a Person entity by its ghid (unique identifier).
     * 
     * @param ghid The ghid of the person to retrieve.
     * @return The Person entity, or null if not found.
     */
    public Person getByGhid(String ghid) {
        return personJpaRepository.findByGhid(ghid); // Fetch person by their ghid
    }

    /**
     * Deletes a Person entity by its ID.
     * 
     * @param id The ID of the person to delete.
     */
    public void delete(long id) {
        personJpaRepository.deleteById(id); // Delete the person from the database by ID
    }

    /**
     * Sets default passwords and roles for all Person entities that lack them.
     * This method ensures that users without credentials are assigned default values.
     * 
     * @param password The default password to assign to users without one.
     * @param roleName The default role to assign to users without a role.
     */
    public void defaults(String password, String roleName) {
        for (Person person : listAll()) {
            // Assign default password if not already set
            if (person.getPassword() == null || person.getPassword().isEmpty()) {
                person.setPassword(passwordEncoder.encode(password)); // Encode the default password
            }
            // Assign default role if the person has no roles
            if (person.getRoles().isEmpty()) {
                PersonRole role = personRoleJpaRepository.findByName(roleName);
                if (role != null) {
                    person.getRoles().add(role); // Add default role to the person
                }
            }
        }
    }

    /**
     * Retrieves all available roles in the system.
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
     * @param ghid The ghid of the person to whom the role should be assigned.
     * @param roleName The name of the role to assign.
     */
    public void addRoleToPerson(String ghid, String roleName) {
        // Find the person by their ghid
        Person person = personJpaRepository.findByGhid(ghid); 
        if (person != null) {
            // Find the role by name
            PersonRole role = personRoleJpaRepository.findByName(roleName); 
            if (role != null && !person.getRoles().contains(role)) {
                person.getRoles().add(role); // Add the role if it's not already assigned
            }
        }
    }

    /**
     * Checks if a Person exists based on their ghid.
     * 
     * @param ghid The ghid to check for existence.
     * @return true if the person exists, false otherwise.
     */
    public boolean existsByGhid(String ghid) {
        return personJpaRepository.existsByGhid(ghid); // Check if a person with the given ghid exists
    }
}
