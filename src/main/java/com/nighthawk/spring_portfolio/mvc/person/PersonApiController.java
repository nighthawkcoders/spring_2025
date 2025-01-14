package com.nighthawk.spring_portfolio.mvc.person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;

/**
 * PersonApiController handles RESTful API endpoints for managing Person entities.
 * The controller allows for CRUD (Create, Read, Update, Delete) operations on Person data.
 */
@RestController
@RequestMapping("/api")
public class PersonApiController {

    // Injecting repository and services for accessing and managing Person data
    @Autowired
    private PersonJpaRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PersonDetailsService personDetailsService;

    /**
     * Retrieves the current user's Person entity based on the JWT token.
     * 
     * @param authentication The authentication object, typically containing the current user's details.
     * @return A ResponseEntity with the found Person object or a NOT_FOUND status if the person doesn't exist.
     */
    @GetMapping("/person/get")
    public ResponseEntity<Person> getPerson(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername(); // Email mapped to username in Spring Security

        Person person = repository.findByGhid(email);

        // Return the found person or a NOT_FOUND status if not found
        if (person != null) {
            return new ResponseEntity<>(person, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all Person entities from the database.
     * 
     * @return A ResponseEntity containing the list of Person entities.
     */
    @GetMapping("/people")
    public ResponseEntity<List<Person>> getPeople() {
        return new ResponseEntity<>(repository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    /**
     * Retrieves a specific Person entity by its ID.
     * 
     * @param id The ID of the Person to retrieve.
     * @return A ResponseEntity containing the Person entity or a NOT_FOUND status if no matching person is found.
     */
    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable long id) {
        Optional<Person> optional = repository.findById(id);
        if (optional.isPresent()) {
            Person person = optional.get(); // Extract the person from the Optional
            return new ResponseEntity<>(person, HttpStatus.OK); // Return found person with HTTP OK
        }
        // Return NOT_FOUND if the person is not found
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Deletes a specific Person entity by its ID.
     * 
     * @param id The ID of the Person to delete.
     * @return A ResponseEntity with the deleted Person entity or a NOT_FOUND status if no matching person is found.
     */
    @DeleteMapping("/person/{id}")
    public ResponseEntity<Person> deletePerson(@PathVariable long id) {
        Optional<Person> optional = repository.findById(id);
        if (optional.isPresent()) {
            Person person = optional.get(); // Extract the person
            repository.deleteById(id); // Delete the person from the repository
            return new ResponseEntity<>(person, HttpStatus.OK); // Return deleted person with HTTP OK
        }
        // Return NOT_FOUND if the person doesn't exist
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * DTO (Data Transfer Object) for creating a new Person entity via POST request.
     */
    @Getter
    public static class PersonDto {
        private String email;
        private String password;
        private String name;
        private Boolean kasmServerNeeded; 
    }

    /**
     * Creates a new Person entity in the database.
     * 
     * @param personDto A DTO containing the information for the new person.
     * @return A ResponseEntity containing a success message or a BAD_REQUEST status if input is invalid.
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/person/create")
    public ResponseEntity<Object> postPerson(@RequestBody PersonDto personDto) {
        // Create a new Person entity without an ID (it will be auto-generated in the database)
        Person person = new Person();
        personDetailsService.save(person); // Save the new person entity to the database

        // Prepare JSON response with success message
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        JSONObject responseObject = new JSONObject();
        responseObject.put("response", personDto.getEmail() + " is created successfully");

        // Return the response with status OK
        String responseString = responseObject.toString();
        return new ResponseEntity<>(responseString, responseHeaders, HttpStatus.OK);
    }

    /**
     * Updates an existing Person entity.
     * 
     * @param authentication The authentication object containing current user details.
     * @param personDto The data to update the person entity.
     * @return A ResponseEntity with the updated Person entity or a NOT_FOUND status if the person doesn't exist.
     */
    @PostMapping(value = "/person/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatePerson(Authentication authentication, @RequestBody final PersonDto personDto) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername(); // Using email as the unique identifier

        // Find the person by email
        Optional<Person> optionalPerson = Optional.ofNullable(repository.findByGhid(email));
        if (optionalPerson.isPresent()) {
            Person existingPerson = optionalPerson.get(); // Extract existing person from repository

            // Update the person's fields if provided in the DTO
            if (personDto.getEmail() != null) {
                existingPerson.setGhid(personDto.getEmail());
            }
            if (personDto.getPassword() != null) {
                existingPerson.setPassword(passwordEncoder.encode(personDto.getPassword()));
            }
            if (personDto.getName() != null) {
                existingPerson.setName(personDto.getName());
            }
            if (personDto.getKasmServerNeeded() != null) {
                existingPerson.setKasmServerNeeded(personDto.getKasmServerNeeded());
            }

            // Save and return the updated person entity
            Person updatedPerson = repository.save(existingPerson);
            return new ResponseEntity<>(updatedPerson, HttpStatus.OK);
        }

        // Return NOT_FOUND if the person does not exist
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Searches for Person entities by name or email.
     * 
     * @param map A map containing the search term with the key "term".
     * @return A ResponseEntity containing a list of matching Person entities.
     */
    @PostMapping(value = "/people/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> personSearch(@RequestBody final Map<String, String> map) {
        String term = map.get("term"); // Extract search term from request body

        // Search the repository for persons matching the term in either name or email
        List<Person> list = repository.findByNameContainingIgnoreCaseOrGhidContainingIgnoreCase(term, term);

        // Return the list of matching persons with HTTP OK
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * Updates the stats for a specific person (e.g., health data).
     * 
     * @param authentication The authentication object to get the current user's email.
     * @param stat_map A map containing the stats data, e.g., health stats with date and measurements.
     * @return A ResponseEntity containing the updated Person entity with stats or a NOT_FOUND status.
     */
    @PostMapping(value = "/person/setStats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> personStats(Authentication authentication, @RequestBody final Map<String,Object> stat_map) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername(); // Get the email from authentication

        // Find the person by email
        Optional<Person> optional = Optional.ofNullable(repository.findByGhid(email));
        if (optional.isPresent()) {
            Person person = optional.get(); // Retrieve the person

            // Get and update existing stats, if any
            Map<String, Map<String, Object>> existingStats = person.getStats();

            // Iterate over the incoming stats and update accordingly
            for (String key : stat_map.keySet()) {
                Map<String, Object> incomingStats = (Map<String, Object>) stat_map.get(key);
                String date = (String) incomingStats.get("date");
                Map<String, Object> attributeMap = new HashMap<>(incomingStats);
                attributeMap.remove("date");

                // Update stats with new or existing data
                if (!existingStats.containsKey(key)) {
                    existingStats.put(key, new HashMap<>());
                }

                if (existingStats.get(key).containsKey(date)) {
                    Map<String, Object> existingAttributes = (Map<String, Object>) existingStats.get(key).get(date);
                    existingAttributes.putAll(attributeMap); // Update existing attributes
                } else {
                    existingStats.get(key).put(date, attributeMap); // Add new date and attributes
                }
            }

            // Save the updated stats and return the updated person
            person.setStats(existingStats);
            repository.save(person);
            return new ResponseEntity<>(person, HttpStatus.OK);
        }

        // Return NOT_FOUND if person not found
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
