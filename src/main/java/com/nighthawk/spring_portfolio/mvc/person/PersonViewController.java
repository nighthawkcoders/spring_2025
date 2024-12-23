package com.nighthawk.spring_portfolio.mvc.person;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.GrantedAuthority;
import jakarta.validation.Valid;
import com.vladmihalcea.hibernate.type.json.JsonType;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;




import jakarta.persistence.Convert;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

// Built using article: https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/mvc.html
// or similar: https://asbnotebook.com/2020/04/11/spring-boot-thymeleaf-form-validation-example/
@Controller
@RequestMapping("/mvc/person")
public class PersonViewController {
    // Autowired enables Control to connect HTML and POJO Object to database easily for CRUD
    @Autowired
    private PersonDetailsService repository;

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
            Person person = repository.getByEmail(userDetails.getUsername());  // Fetch the person by email
            @Data
            @AllArgsConstructor
            @Convert(attributeName = "person", converter = JsonType.class)
            class PersonAdjacent{ //equilvalent class to Person, but id is replaced by a string
                private String id;  
                private String age;       
                private String email;
                private String password;
                private String name;
                private String kasmServerNeeded;
                private String pfp;
            }
            //populate personAdajacent, id is replaced by "user"
            PersonAdjacent personAdjacent = new PersonAdjacent(
                "user", 
                String.valueOf(person.getAge()), // Assuming `getAge()` returns an integer, convert to string
                person.getEmail(),
                person.getPassword(),
                person.getName(),
                person.getKasmServerNeeded() != null ? person.getKasmServerNeeded().toString() : "false", // Convert Boolean to String
                person.getPfp()
            );
            List<PersonAdjacent> list = Arrays.asList(personAdjacent);  // Convert the single person into a list for consistency
            model.addAttribute("list", list);  // Add the list to the model for the view 
        }
        return "person/read";  // Return the template for displaying persons
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
    public String personSave(@Valid Person person, BindingResult bindingResult) {
        // Validation of Decorated PersonForm attributes
        if (bindingResult.hasErrors()) {
            return "person/create";
        }
        repository.save(person);
        repository.addRoleToPerson(person.getEmail(), "ROLE_STUDENT");
        // Redirect to next step
        return "redirect:/mvc/person/read";
    }

    @GetMapping("/update/{id}")
    public String personUpdate(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", repository.get(id));
        return "person/update";
    }

    @GetMapping("/update/user")
    public String personUpdate(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("person", repository.getByEmail(userDetails.getUsername()));  // Add the person to the model
        return "person/update";  // Return the template for the update form
    }
    @Getter
    public static class PersonRoleDto {
        private String email;
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
        Person personToUpdate = repository.getByEmail(roleDto.getEmail());
        if (personToUpdate == null) {
            return new ResponseEntity<>(personToUpdate, HttpStatus.CONFLICT);  // Return error if person not found
        }

        repository.addRoleToPerson(roleDto.getEmail(), roleDto.getRoleName());  // Add the role to the person

        return new ResponseEntity<>(personToUpdate, HttpStatus.OK);  // Return success response
    }

    @Getter
    public static class PersonRolesDto {
        private String email;
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
        Person personToUpdate = repository.getByEmail(rolesDto.getEmail());
        if (personToUpdate == null) {
            return new ResponseEntity<>(personToUpdate, HttpStatus.CONFLICT);  // Return error if person not found
        }

        // Add all roles to the person
        for (String roleName : rolesDto.getRoleNames()) {
            repository.addRoleToPerson(rolesDto.getEmail(), roleName);
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
        repository.delete(repository.getByEmail(userDetails.getUsername()).getId());  // Delete the person by ID
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
        if (repository.getByEmail(((UserDetails)authentication.getPrincipal()).getUsername()).getId() == id){
            deletingYourself = true;
        }
        repository.delete(id);  // Delete the person by ID
        if(deletingYourself){
            return "redirect:/logout"; //logout the user
        }
        
        return "person/read";  // Redirect to the read page after deletion
    }

@PostMapping("/update")
public String personUpdateSave(Authentication authentication, @Valid Person person, BindingResult bindingResult,
                                @RequestParam(value = "pfpBase64", required = false) String pfpBase64) {
    // Check authentication and user details
    if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
        return "redirect:/e#unauthorized"; // Redirect if authentication is null or invalid
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

    // Retrieve the person to update by email
    Person personToUpdate = repository.getByEmail(person.getEmail());
    if (personToUpdate == null) {
        return "redirect:/e#email_does_not_exist"; // Redirect if the person does not exist
    }

    // If the user is not an admin, ensure they are updating their own record
    if (!isAdmin) {
        Person currentUser = repository.getByEmail(userDetails.getUsername());
        if (currentUser == null || !currentUser.getId().equals(personToUpdate.getId())) {
            return "redirect:/e#unauthorized"; // Redirect if not authorized
        }
    }

    // Update fields only if the new values are provided
    boolean updated = false;

    if (pfpBase64 != null && !pfpBase64.isEmpty()) {
        personToUpdate.setPfp(pfpBase64); // Update the profile picture with Base64 value
        updated = true;
    }

    if (person.getPassword() != null && !person.getPassword().isEmpty()) {
        personToUpdate.setPassword(person.getPassword());
        updated = true;
    }

    if (person.getName() != null && !person.getName().isEmpty()) {
        personToUpdate.setName(person.getName());
        updated = true;
    }

    if (person.getEmail() != null && !person.getEmail().isEmpty()) {
        // Check if the new email already exists to avoid conflicts
        Person existingPerson = repository.getByEmail(person.getEmail());
        if (existingPerson != null && !existingPerson.getId().equals(personToUpdate.getId())) {
            return "redirect:/e#email_already_in_use"; // Redirect if email is already taken
        }
    }

    if (person.getDob() != null) {
        personToUpdate.setDob(person.getDob());
        updated = true;
    }

    if (updated) {
        repository.save(personToUpdate); // Save the updated person object
    }

    return "redirect:/mvc/person/read"; // Redirect to the profile page or another appropriate page
}


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
    else if(repository.getByEmail(userDetails.getUsername()).getId() == id){
        Person person = repository.getByEmail(userDetails.getUsername());  // Fetch the person by email
        @Data
        @AllArgsConstructor
        @Convert(attributeName = "person", converter = JsonType.class)
        class PersonAdjacent{ //equilvalent class to Person, but id is replaced by a string
            private String id;        
            private String email;
            private String password;
            private String name;
            private boolean kasmServerNeeded;
            private String pfp;
        }
        //populate personAdajacent, id is replaced by "user"
        PersonAdjacent personAdjacent = new PersonAdjacent("user",person.getEmail(),person.getPassword(),person.getName(),person.getKasmServerNeeded(),person.getPfp()); 
        List<PersonAdjacent> list = Arrays.asList(personAdjacent);  // Convert the single person into a list for consistency
        model.addAttribute("list", list);  // Add the list to the model for the view 
    }
    return "person/read";  // Return the template for displaying the person
}


 @GetMapping("/search")
    public String person() {
        return "person/search";
    }

}