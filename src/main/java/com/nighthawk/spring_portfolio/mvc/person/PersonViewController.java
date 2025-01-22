package com.nighthawk.spring_portfolio.mvc.person;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.Convert;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Controller for managing person-related views and operations (CRUD).
 * Handles actions for reading, creating, updating, and deleting person objects.
 */
@Controller
@RequestMapping("/mvc/person")
public class PersonViewController {

    // Inject the service layer for interacting with the person repository
    @Autowired
    private PersonDetailsService repository;

    /**
     * Displays a list of all persons.
     * If the user is an admin, show all persons; otherwise, show only the logged-in person.
     *
     * @param authentication the authentication object for accessing the current user's details
     * @param model the model object to pass attributes to the view
     * @return the view name for displaying the list of persons
     */
    @GetMapping("/read")
    public String person(Authentication authentication, Model model) {
        // Check if the user has admin authority
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (isAdmin) {
            List<Person> list = repository.listAll();  // Fetch all persons for admin
            model.addAttribute("list", list);  // Add the list to the model for the view
        } else {
            // Fetch the person by their GitHub ID for non-admin users
            Person person = repository.getByGhid(userDetails.getUsername());
            @Data
            @AllArgsConstructor
            @Convert(attributeName = "person", converter = JsonType.class)
            class PersonAdjacent {
                private String id;
                private String email;
                private String ghid;
                private String password;
                private String name;
                private boolean kasmServerNeeded;
            }
            // Wrap the person data into a PersonAdjacent object for consistent list format
            PersonAdjacent personAdjacent = new PersonAdjacent("user", person.getEmail(), person.getGhid(), person.getPassword(), person.getName(), person.getKasmServerNeeded());
            List<PersonAdjacent> list = Arrays.asList(personAdjacent);  // Convert the single person into a list
            model.addAttribute("list", list);  // Add the list to the model for the view
        }
        return "person/read";  // Return the template to display the list of persons
    }

    /**
     * Displays a specific person based on the provided ID.
     * Only an admin can see other users' details; non-admin users can only view their own data.
     *
     * @param id the ID of the person to display
     * @param authentication the authentication object to access user details
     * @param model the model object to pass attributes to the view
     * @return the view name for displaying the details of the specified person
     */
    @GetMapping("/read/{id}")
    public String person(Authentication authentication, @PathVariable("id") int id, Model model) {
        // Check if the user has admin authority
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (isAdmin) {
            Person person = repository.get(id);  // Admin can view any person's details by ID
            List<Person> list = Arrays.asList(person);  // Wrap the person in a list for consistency
            model.addAttribute("list", list);  // Add the list to the model for the view
        } else if (repository.getByGhid(userDetails.getUsername()).getId() == id) {
            // Non-admin users can only view their own details
            Person person = repository.getByGhid(userDetails.getUsername());
            @Data
            @AllArgsConstructor
            @Convert(attributeName = "person", converter = JsonType.class)
            class PersonAdjacent {
                private String id;
                private String email;
                private String ghid;
                private String password;
                private String name;
                private boolean kasmServerNeeded;
            }
            // Wrap the person data into a PersonAdjacent object for consistent list format
            PersonAdjacent personAdjacent = new PersonAdjacent("user", person.getEmail(), person.getGhid(), person.getPassword(), person.getName(), person.getKasmServerNeeded());
            List<PersonAdjacent> list = Arrays.asList(personAdjacent);  // Convert the single person into a list
            model.addAttribute("list", list);  // Add the list to the model for the view
        }
        return "person/read";  // Return the template to display the person's details
    }

    /**
     * Displays the form for creating a new person.
     *
     * @param person a blank Person object to bind form fields to
     * @return the view name for displaying the creation form
     */
    @GetMapping("/create")
    public String personAdd(Person person) {
        return "person/create";  // Return the create form view
    }

    /**
     * Saves a new person after validation and checking for errors.
     *
     * @param person the Person object to save
     * @param bindingResult the result of binding form data to the Person object
     * @param model the model object to add error messages
     * @return the view name for the creation form (with errors, if any) or redirect to the read page if successful
     */
    @PostMapping("/create")
    public String personSave(@Valid Person person, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {  // If validation errors exist
            return "person/create";  // Return to the create form with errors
        }

        // Check if the GitHub ID already exists in the database
        if (repository.existsByGhid(person.getGhid())) {
            model.addAttribute("ghidError", "This Github Id is already in use. Please use a different Github Id.");
            return "person/create";  // Return to the form with an error message
        }

        repository.save(person);  // Save the new person
        repository.addRoleToPerson(person.getGhid(), "ROLE_USER");  // Assign default roles
        repository.addRoleToPerson(person.getGhid(), "ROLE_STUDENT");

        return "redirect:/mvc/person/read";  // Redirect to the read page after saving
    }

    /**
     * Displays the form for updating an existing person's details by ID.
     *
     * @param id the ID of the person to update
     * @param model the model object to pass attributes to the view
     * @return the view name for displaying the update form
     */
    @GetMapping("/update/{id}")
    public String personUpdate(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", repository.get(id));  // Add the person to the model
        return "person/update";  // Return the update form view
    }

    /**
     * Displays the form for updating the logged-in user's details.
     *
     * @param authentication the authentication object to access user details
     * @param model the model object to pass attributes to the view
     * @return the view name for displaying the update form for the logged-in user
     */
    @GetMapping("/update/user")
    public String personUpdate(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("person", repository.getByGhid(userDetails.getUsername()));  // Add the person's data to the model
        return "person/update";  // Return the update form view
    }

    /**
     * Saves the updated details of a person.
     * Only allows updates if changes are detected, otherwise redirects to an error page.
     *
     * @param person the Person object containing updated data
     * @param bindingResult the result of binding form data to the Person object
     * @param authentication the authentication object to check the user's authority
     * @return the redirect to the read page or an error page if no changes were detected
     */
    @PostMapping("/update")
    public String personUpdateSave(Authentication authentication, @Valid Person person, BindingResult bindingResult) {
        // Check if the user has admin authority
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        Person personToUpdate = repository.getByGhid(person.getGhid());

        // If the user is not an admin, they can only update their own details
        if (!isAdmin && !personToUpdate.getId().equals(repository.getByGhid(userDetails.getUsername()).getId())) {
            return "redirect:/e#Unauthorized";  // Redirect if user tries to update another person's details
        }

        boolean updated = false;
        boolean samePassword = true;

        // Update fields if the new values are provided
        if (!person.getPassword().isBlank()) {
            personToUpdate.setPassword(person.getPassword());
            updated = true;
            samePassword = false;
        }
        if (!person.getName().isBlank() && !person.getName().equals(personToUpdate.getName())) {
            personToUpdate.setName(person.getName());
            updated = true;
        }

        // If no fields were updated, return with a no changes detected error
        if (!updated) {
            return "redirect:/e#no_changes_detected";
        }

        // Save the updated person and ensure the roles are correctly maintained
        repository.save(personToUpdate, samePassword);
        repository.addRoleToPerson(person.getGhid(), "ROLE_USER");
        repository.addRoleToPerson(person.getGhid(), "ROLE_STUDENT");

        return "redirect:/mvc/person/read";  // Redirect to the read page after updating
    }

    // DTO classes for handling role updates via REST API

    @Getter
    public static class PersonRoleDto {
        private String ghid;
        private String roleName;
    }

    /**
     * Updates a specific role for a person via a RESTful request.
     *
     * @param roleDto the DTO containing the GitHub ID and the role name
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/update/role")
    public ResponseEntity<Object> personRoleUpdateSave(@RequestBody PersonRoleDto roleDto) {
        Person personToUpdate = repository.getByGhid(roleDto.getGhid());
        if (personToUpdate == null) {
            return new ResponseEntity<>(personToUpdate, HttpStatus.CONFLICT);  // Return error if person not found
        }

        repository.addRoleToPerson(roleDto.getGhid(), roleDto.getRoleName());  // Add the role to the person

        return new ResponseEntity<>(personToUpdate, HttpStatus.OK);  // Return success response
    }

    @Getter
    public static class PersonRolesDto {
        private String ghid;
        private List<String> roleNames;
    }

    /**
     * Updates multiple roles for a person via a RESTful request.
     *
     * @param rolesDto the DTO containing the GitHub ID and a list of role names
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/update/roles")
    public ResponseEntity<Object> personRolesUpdateSave(@RequestBody PersonRolesDto rolesDto) {
        Person personToUpdate = repository.getByGhid(rolesDto.getGhid());
        if (personToUpdate == null) {
            return new ResponseEntity<>(personToUpdate, HttpStatus.CONFLICT);  // Return error if person not found
        }

        // Add all roles to the person
        rolesDto.getRoleNames().forEach(roleName -> repository.addRoleToPerson(rolesDto.getGhid(), roleName));

        return new ResponseEntity<>(personToUpdate, HttpStatus.OK);  // Return success response
    }

    /**
     * Displays a random person's quiz for the user.
     *
     * @param model the model object to pass attributes to the view
     * @return the view name for the person quiz page
     */
    @GetMapping("/person-quiz")
    public String personQuiz(Model model) {
        List<Person> list = repository.listAll();  // Fetch all persons
        model.addAttribute("person", list.get((int) (Math.random() * list.size())));  // Add a random person to the model
        return "person/person-quiz";  // Return the quiz view
    }

    /**
     * Deletes the currently logged-in person.
     *
     * @param authentication the authentication object to access user details
     * @return the redirect to logout after deleting the person
     */
    @GetMapping("/delete/user")
    public String personDelete(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        repository.delete(repository.getByGhid(userDetails.getUsername()).getId());  // Delete the person by ID
        return "redirect:/logout";  // Logout the user after deletion
    }

    /**
     * Deletes a person by ID.
     * If the logged-in user deletes themselves, they are logged out after deletion.
     *
     * @param id the ID of the person to delete
     * @param authentication the authentication object to access user details
     * @return the redirect to the read page or logout if deleting oneself
     */
    @GetMapping("/delete/{id}")
    public String personDelete(Authentication authentication, @PathVariable("id") long id) {
        boolean deletingYourself = repository.getByGhid(((UserDetails) authentication.getPrincipal()).getUsername()).getId() == id;
        repository.delete(id);  // Delete the person by ID
        if (deletingYourself) {
            return "redirect:/logout"; // Log out if the user deletes themselves
        }
        return "redirect:/mvc/person/read";  // Redirect to the read page after deletion
    }

    /**
     * Displays the search page.
     *
     * @return the view name for the search page
     */
    @GetMapping("/search")
    public String person() {
        return "person/search";  // Return the search page view
    }
}
