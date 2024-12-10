package com.nighthawk.spring_portfolio.mvc.person;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.userStocks.UserStocksRepository;
import com.nighthawk.spring_portfolio.mvc.userStocks.userStocksTable;

import lombok.Getter;


@RestController
@RequestMapping("/api")
public class PersonApiController {


   @Autowired
   private PersonJpaRepository repository;


   @Autowired
   private PasswordEncoder passwordEncoder;


   @Autowired
   private PersonDetailsService personDetailsService;


   @Autowired
   private UserStocksRepository userStocksRepository;


   @GetMapping("/person/get")
   public ResponseEntity<Person> getPerson(Authentication authentication) {
       UserDetails userDetails = (UserDetails) authentication.getPrincipal();
       String email = userDetails.getUsername();


       Person person = repository.findByEmail(email);


       if (person != null) {
           return new ResponseEntity<>(person, HttpStatus.OK);
       } else {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
   }


   @GetMapping("/people")
   public ResponseEntity<List<Person>> getPeople() {
       return new ResponseEntity<>(repository.findAllByOrderByNameAsc(), HttpStatus.OK);
   }


   @GetMapping("/person/{id}")
   public ResponseEntity<Person> getPerson(@PathVariable long id) {
       Optional<Person> optional = repository.findById(id);
       if (optional.isPresent()) {
           return new ResponseEntity<>(optional.get(), HttpStatus.OK);
       }
       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }


   @DeleteMapping("/person/{id}")
   public ResponseEntity<Person> deletePerson(@PathVariable long id) {
       Optional<Person> optional = repository.findById(id);
       if (optional.isPresent()) {
           repository.deleteById(id);
           return new ResponseEntity<>(optional.get(), HttpStatus.OK);
       }
       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }


   @Getter
   public static class PersonDto {
       private String email;
       private String password;
       private String name;
       private String dob;
       private double balance;
       private String pfp;
       private Boolean kasmServerNeeded;
   }


   @PostMapping("/person/create")
   public ResponseEntity<Object> postPerson(@RequestBody PersonDto personDto) {
       Date dob;
       try {
           dob = new SimpleDateFormat("MM-dd-yyyy").parse(personDto.getDob());
       } catch (Exception e) {
           return new ResponseEntity<>(personDto.getDob() + " error; try MM-dd-yyyy", HttpStatus.BAD_REQUEST);
       }


       Person person = new Person(
           personDto.getEmail(),
           passwordEncoder.encode(personDto.getPassword()),
           personDto.getName(),
           dob,
           0.0, // Initializing balance to 0.0
           personDetailsService.findRole("USER"),
           null, // Profile picture set to null as a placeholder
           false // Default value for kasmServerNeeded
       );
      
       personDetailsService.save(person);


       userStocksTable userStocks = new userStocksTable("AAPL", "BTC", person);
       userStocksRepository.save(userStocks);


       HttpHeaders responseHeaders = new HttpHeaders();
       responseHeaders.setContentType(MediaType.APPLICATION_JSON);


       JSONObject responseObject = new JSONObject();
       responseObject.put("response", personDto.getEmail() + " is created successfully");


       return new ResponseEntity<>(responseObject.toString(), responseHeaders, HttpStatus.CREATED);
   }


   @PostMapping(value = "/person/update", produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Object> updatePerson(Authentication authentication, @RequestBody PersonDto personDto) {
       UserDetails userDetails = (UserDetails) authentication.getPrincipal();
       String email = userDetails.getUsername();


       Optional<Person> optionalPerson = Optional.ofNullable(repository.findByEmail(email));
       if (optionalPerson.isPresent()) {
           Person existingPerson = optionalPerson.get();


           if (personDto.getEmail() != null) {
               existingPerson.setEmail(personDto.getEmail());
           }
           if (personDto.getPassword() != null) {
               existingPerson.setPassword(passwordEncoder.encode(personDto.getPassword()));
           }
           if (personDto.getName() != null) {
               existingPerson.setName(personDto.getName());
           }
           if (personDto.getPfp() != null) {
               existingPerson.setPfp(personDto.getPfp());
           }
           if (personDto.getKasmServerNeeded() != null) {
               existingPerson.setKasmServerNeeded(personDto.getKasmServerNeeded());
           }


           Person updatedPerson = repository.save(existingPerson);
           return new ResponseEntity<>(updatedPerson, HttpStatus.OK);
       }
       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }


   @PostMapping(value = "/people/search", produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Object> personSearch(@RequestBody final Map<String, String> map) {
       String term = map.get("term");
       List<Person> list = repository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);
       return new ResponseEntity<>(list, HttpStatus.OK);
   }


   @PutMapping("/person/{id}")
   public ResponseEntity<Object> updatePerson(@PathVariable long id, @RequestBody PersonDto personDto) {
       Optional<Person> optional = repository.findById(id);
       if (optional.isPresent()) {
           Person existingPerson = optional.get();


           existingPerson.setEmail(personDto.getEmail());
           existingPerson.setPassword(passwordEncoder.encode(personDto.getPassword()));
           existingPerson.setName(personDto.getName());
           existingPerson.setPfp(personDto.getPfp());
           existingPerson.setKasmServerNeeded(personDto.getKasmServerNeeded());






           repository.save(existingPerson);
           return new ResponseEntity<>(existingPerson, HttpStatus.OK);
       }
       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }
}



