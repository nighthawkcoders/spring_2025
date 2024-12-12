package com.nighthawk.spring_portfolio.mvc.person;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.Getter;

// Built using article: https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/mvc.html
// or similar: https://asbnotebook.com/2020/04/11/spring-boot-thymeleaf-form-validation-example/
@Controller
@RequestMapping("/mvc/person")
public class PersonViewController {
    // Autowired enables Control to connect HTML and POJO Object to database easily for CRUD
    @Autowired
    private PersonDetailsService repository;

    /*
        @return - template for person read page for all ids
    */

    @GetMapping("/read")
    public String person(Model model) { //read all ids
        List<Person> list = repository.listAll();
        model.addAttribute("list", list);
        return "person/read";
    }

      /*
        @return - template for person read page for the specific id
        @param - Id
    */

    @GetMapping("/read/{id}") //read a specific id
    public String person(@PathVariable("id") int id, Model model) {
        Person person = repository.get(id);
        List<Person> list = Arrays.asList(person);
        model.addAttribute("list",list);
        return "person/read";
    }

    /*  The HTML template Forms and PersonForm attributes are bound
        @return - template for person form
        @param - Person Class
    */
    @GetMapping("/create")
    public String personAdd(Person person) {
        return "person/create";
    }

    /* Gathers the attributes filled out in the form, tests for and retrieves validation error
    @param - Person object with @Valid
    @param - BindingResult object
     */

    @PostMapping("/create")
    public String personSave(@Valid Person person, BindingResult bindingResult, Model model) {
        // Validation of Decorated PersonForm attributes
        if (bindingResult.hasErrors()) {
            return "person/create";
        }

        // Check if ghid already exists in the database
        if (repository.existsByGhid(person.getGhid())) {
            model.addAttribute("ghidError", "This Github Id is already in use. Please use a different Github Id.");
            return "person/create"; // Return to form with error message
        }

        // Save new person to the database
        person.setBalance(Float.valueOf(0)); //default balance to 0;
        repository.save(person);
        repository.addRoleToPerson(person.getGhid(), "ROLE_USER");
        repository.addRoleToPerson(person.getGhid(), "ROLE_STUDENT");

        // Redirect to the person list page
        return "redirect:/mvc/person/read";
    }

    /*  The HTML template Forms and PersonForm attributes are bound
        @return - template for person update form for the specific id
        @param - Id
    */

    @GetMapping("/update/{id}")
    public String personUpdate(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", repository.get(id));
        return "person/update";
    }

    /* Gathers the attributes filled out in the form, tests for and retrieves validation error
        @param - Person object with @Valid
        @param - BindingResult object
        @return - Redirects to error page if errors, or read page if successful
     */
    @PostMapping("/update")
public String personUpdateSave(@Valid Person person, BindingResult bindingResult) {
    // Handle validation errors
    // if (bindingResult.hasErrors()) {
    //     return "redirect:/e#there_were_errors_with_updating";
    // }

    // Retrieve the person by email
    Person personToUpdate = repository.getByGhid(person.getGhid());
    if (personToUpdate == null) {
        return "redirect:/e#email_does_not_exist";
    }

    boolean updated = false;

    if(person.getPassword() != null){
        personToUpdate.setPassword(person.getPassword());
        updated = true;
    }
    if (person.getName() != null) {
        personToUpdate.setName(person.getName());
        updated = true;
    }
    if (person.getDob() != null) {
        personToUpdate.setDob(person.getDob());
        updated = true;
    }
    if (person.getPfp() != null) {
        personToUpdate.setPfp(person.getPfp());
        updated = true;
    }
    if (person.getKasmServerNeeded() != null) {
        personToUpdate.setKasmServerNeeded(person.getKasmServerNeeded());
        updated = true;
    }

    // If no attributes were updated, inform the user
    if (!updated) {
        return "redirect:/e#no_changes_detected";
    }

    // Save the updated person
    repository.save(personToUpdate);

    // Ensure the user role is assigned (if required by logic)
    repository.addRoleToPerson(person.getGhid(), "ROLE_USER");
    repository.addRoleToPerson(person.getGhid(), "ROLE_STUDENT");


        // Redirect to next step
        return "redirect:/mvc/person/read";
    }

    @Getter
    public static class PersonRoleDto{
        private String ghid;
        private String roleName;
    }

    @PostMapping("/update/role")
    public ResponseEntity<Object> personRoleUpdateSave(@RequestBody PersonRoleDto roleDto) {

        Person personToUpdate = repository.getByGhid(roleDto.getGhid());
        if(personToUpdate == null){
            return new ResponseEntity<>(personToUpdate, HttpStatus.CONFLICT);
        }

        repository.addRoleToPerson(roleDto.getGhid(), roleDto.getRoleName());

        return new ResponseEntity<>(personToUpdate, HttpStatus.OK);
    }

    /*
        @param - Id
        @return - Redirect to read page
    */
    @GetMapping("/delete/{id}")
    public String personDelete(@PathVariable("id") long id) {
        repository.delete(id); //delete user with given id
        return "redirect:/mvc/person/read";
    }

    /*
        @return - Search page
    */
    @GetMapping("/search")
    public String person() {
        return "person/search";
    }

}
