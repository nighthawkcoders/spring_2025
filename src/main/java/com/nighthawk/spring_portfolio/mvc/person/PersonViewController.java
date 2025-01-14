package com.nighthawk.spring_portfolio.mvc.person;

import java.net.PasswordAuthentication;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.crypto.password.*;

import com.vladmihalcea.hibernate.type.json.JsonType;


import jakarta.persistence.Convert;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Controller class for handling person-related views and CRUD operations.
 * This controller handles requests for reading, creating, updating, and deleting Person objects.
 */
@Controller
@RequestMapping("/mvc/person")
public class PersonViewController {

    // Inject the service layer for accessing the person repository
    @Autowired
    private PersonDetailsService repository;

    /**
     * Displays a list of all persons.
     *
     * @param model the model to add attributes for the view
     * @return the view name for reading all persons
     */
    @GetMapping("/read")
    public String person(Authentication authentication, Model model) {
        //check user authority
        UserDetails userDetails = (UserDetails)authentication.getPrincipal(); 
        boolean isAdmin = false;
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            if(String.valueOf("ROLE_ADMIN").equals(authority.getAuthority())){
                isAdmin = true;
                break;
            }
        }
        if (isAdmin == true){
            List<Person> list = repository.listAll();  // Fetch all persons
            model.addAttribute("list", list);  // Add the list to the model for the view
        }
        else {
            Person person = repository.getByGhid(userDetails.getUsername());  // Fetch the person by ghid
            @Data
            @AllArgsConstructor
            @Convert(attributeName = "person", converter = JsonType.class)
            class PersonAdjacent{ //equilvalent class to Person, but id is replaced by a string
                private String id;        
                private String ghid;
                private String password;
                private String name;
                private boolean kasmServerNeeded;
            }
            //populate personAdajacent, id is replaced by "user"
            PersonAdjacent personAdjacent = new PersonAdjacent("user",person.getGhid(),person.getPassword(),person.getName(),person.getKasmServerNeeded());
            List<PersonAdjacent> list = Arrays.asList(personAdjacent);  // Convert the single person into a list for consistency
            model.addAttribute("list", list);  // Add the list to the model for the view 
        }
        return "person/read";  // Return the template for displaying persons
    }

    /**
     * Displays a specific person based on the provided ID.
     *
     * @param id the ID of the person
     * @param model the model to add attributes for the view
     * @return the view name for displaying a single person's information
     */
    @GetMapping("/read/{id}")
    public String person(Authentication authentication, @PathVariable("id") int id, Model model) {
        //check user authority
        UserDetails userDetails = (UserDetails)authentication.getPrincipal(); 
        boolean isAdmin = false;
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            if(String.valueOf("ROLE_ADMIN").equals(authority.getAuthority())){
                isAdmin = true;
                break;
            }
        }
        if (isAdmin == true){
            Person person = repository.get(id);  // Fetch the person by ID
            List<Person> list = Arrays.asList(person);  // Convert the single person into a list for consistency
            model.addAttribute("list", list);  // Add the list to the model for the view 
        }
        else if(repository.getByGhid(userDetails.getUsername()).getId() == id){
            Person person = repository.getByGhid(userDetails.getUsername());  // Fetch the person by ghid
            @Data
            @AllArgsConstructor
            @Convert(attributeName = "person", converter = JsonType.class)
            class PersonAdjacent{ //equilvalent class to Person, but id is replaced by a string
                private String id;        
                private String ghid;
                private String password;
                private String name;
                private boolean kasmServerNeeded;
            }
            //populate personAdajacent, id is replaced by "user"
            PersonAdjacent personAdjacent = new PersonAdjacent("user",person.getGhid(),person.getPassword(),person.getName(),person.getKasmServerNeeded()); 
            List<PersonAdjacent> list = Arrays.asList(personAdjacent);  // Convert the single person into a list for consistency
            model.addAttribute("list", list);  // Add the list to the model for the view 
        }
        return "person/read";  // Return the template for displaying the person
    }

    /**
     * Displays the form for creating a new person.
     *
     * @param person a blank Person object to bind form fields to
     * @return the view name for the creation form
     */
    @GetMapping("/create")
    public String personAdd(Person person) {
        return "person/create";  // Return the template for the create form
    }

    /**
     * Saves a new person after validation and checking for errors.
     *
     * @param person the Person object to save
     * @param bindingResult the result of binding form data to the Person object
     * @param model the model to add error messages
     * @return the view name for the creation form (with errors if any) or redirect to the read page if successful
     */
    @PostMapping("/create")
    public String personSave(@Valid Person person, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {  // If there are validation errors
            return "person/create";  // Return to the create form with errors
        }

        // Check if the GitHub ID already exists in the database
        if (repository.existsByGhid(person.getGhid())) {
            model.addAttribute("ghidError", "This Github Id is already in use. Please use a different Github Id.");
            return "person/create";  // Return to form with error message
        }

        person.setBalance(0f);  // Set the default balance to 0
        repository.save(person);  // Save the new person
        repository.addRoleToPerson(person.getGhid(), "ROLE_USER");  // Add default roles
        repository.addRoleToPerson(person.getGhid(), "ROLE_STUDENT");

        return "redirect:/mvc/person/read";  // Redirect to the read page after saving
    }

    /**
     * Displays the form for updating an existing person by ID.
     *
     * @param id the ID of the person to update
     * @param model the model to add attributes for the view
     * @return the view name for the update form
     */
    @GetMapping("/update/{id}")
    public String personUpdate(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", repository.get(id));  // Add the person to the model
        return "person/update";  // Return the template for the update form
    }

    @GetMapping("/update/user")
    public String personUpdate(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("person", repository.getByGhid(userDetails.getUsername()));  // Add the person to the model
        return "person/update";  // Return the template for the update form
    }

    /**
     * Saves the updated details of a person.
     *
     * @param person the Person object containing updated data
     * @param bindingResult the result of binding form data to the Person object
     * @return the redirect to the read page or an error page if no changes were detected
     */
    @PostMapping("/update")
    public String personUpdateSave(Authentication authentication, @Valid Person person, BindingResult bindingResult) {
        
        //check user authority
        UserDetails userDetails = (UserDetails)authentication.getPrincipal(); 
        boolean isAdmin = false;
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            if(String.valueOf("ROLE_ADMIN").equals(authority.getAuthority())){
                isAdmin = true;
                break;
            }
        }

        Person personToUpdate = repository.getByGhid(person.getGhid());
        
        //if the user is not an admin, then check if they are updating themself
        if(!isAdmin){
            //if not then return Unauthorized
            if(! Long.valueOf(personToUpdate.getId()).equals((repository.getByGhid(userDetails.getUsername())).getId())){
                return "redirect:/e#Unauthorized";
            }
        }

        if (personToUpdate == null)     {
            return "redirect:/e#email_does_not_exist";  // Redirect to error page if the person does not exist
        }

        boolean updated = false;
        boolean samePassword = true;

        // Update fields if the new values are provided
        if ((person.getPassword() != null) && (person.getPassword().isBlank() == false)) {
            personToUpdate.setPassword(person.getPassword());
            updated = true;
            samePassword = false;
        }
        if ((person.getName() != null) && (person.getName().isBlank() == false) && ((personToUpdate.getName() == null) || (!person.getName().equals(personToUpdate.getName())))) {
            personToUpdate.setName(person.getName());
            updated = true;
        }

        // If no attributes were updated, inform the user
        if (!updated) {
            return "redirect:/e#no_changes_detected";
        }

       
        // Save the updated person
        repository.save(personToUpdate,samePassword);

        // Ensure the roles are correctly assigned
        repository.addRoleToPerson(person.getGhid(), "ROLE_USER");
        repository.addRoleToPerson(person.getGhid(), "ROLE_STUDENT");

        return "redirect:/mvc/person/read";  // Redirect to the read page after saving
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
     * @param roleDto the DTO containing the GitHub ID and role name
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
        for (String roleName : rolesDto.getRoleNames()) {
            repository.addRoleToPerson(rolesDto.getGhid(), roleName);
        }

        return new ResponseEntity<>(personToUpdate, HttpStatus.OK);  // Return success response
    }

    @GetMapping("/person-quiz")
    public String personQuiz(Model model){
        List<Person> list = repository.listAll();  // Fetch all persons
        model.addAttribute("person", list.get((int)(Math.random()*list.size())));  // Add the list to the model for the view
        return "person/person-quiz";
    }

    @GetMapping("/delete/user")
    public String personDelete(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        repository.delete(repository.getByGhid(userDetails.getUsername()).getId());  // Delete the person by ID
        return "redirect:/logout";  // logout the user
    }

    /**sq
     * Deletes a person by ID.
     *
     * @param id the ID of the person to delete
     * @return redirect to the read page after deletion
     */
    @GetMapping("/delete/{id}")
    public String personDelete(Authentication authentication, @PathVariable("id") long id) {
        //don't redirect to read page if you delete yourself
        //check before deleting from database to avoid imploding the backend
        boolean deletingYourself = false;
        if (repository.getByGhid(((UserDetails)authentication.getPrincipal()).getUsername()).getId() == id){
            deletingYourself = true;
        }
        repository.delete(id);  // Delete the person by ID
        if(deletingYourself){
            return "redirect:/logout"; //logout the user
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
        return "person/search";  // Return the template for the search page
    }
}
