package com.nighthawk.spring_portfolio.mvc.Grades;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.CascadeType;
import org.springframework.http.HttpStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.nighthawk.spring_portfolio.mvc.Grades.Asmt;
import com.nighthawk.spring_portfolio.security.JwtTokenUtil;

@RestController
@RequestMapping("/api/assignments")
public class AsmtApiController {
    
    @Autowired
    private AsmtJpaRepository repository;
    
    private JwtTokenUtil jwtTokenUtil;
    //jwtTokenUtil.


    @PostMapping("/add")
    public ResponseEntity<Asmt> addAssignment(@RequestBody Asmt assignmentName) {

        Asmt savedAssignment = repository.save(assignmentName);
        return new ResponseEntity<>(savedAssignment, HttpStatus.CREATED); 
    }

    @GetMapping("/get")
    public ResponseEntity<List<Asmt>> getNames() {
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    



}
