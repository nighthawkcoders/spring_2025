package com.nighthawk.spring_portfolio.mvc.Grades;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/userassignments")
public class UserAssignmentsApiController {
    
    @Autowired
    private UserAssignmentsJpaRepository repository; 


    
    
    @PostMapping("/add")
    public ResponseEntity<UserAssignments> addAssignment(@RequestBody UserAssignments assignment) {

        UserAssignments newAssignment = repository.save(assignment);
        return new ResponseEntity<>(newAssignment, HttpStatus.CREATED); 
    }


    @GetMapping("/get/{userid}")
    public ResponseEntity<List<UserAssignmentsSummary>> getUserAssignments(@PathVariable int userid) {
        List<UserAssignmentsSummary> summaries = repository.findUserAssignmentsSummaries(userid);
        return new ResponseEntity<>(summaries, HttpStatus.OK);
    }
    
     
}
