package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;

@RestController
@RequestMapping("/api/tinkle")
public class TinkleApiController {

    @Autowired
    private TinkleJPARepository repository;

    @Getter
    public static class TinkleDto {
        private String studentEmail;
        private String timeIn;
        // private double averageDuration;
    }

    @PostMapping("/add")
    public ResponseEntity<Object> timeInOut(@RequestBody TinkleDto tinkleDto) {
        Optional<Tinkle> student = repository.findByPersonName(tinkleDto.getStudentEmail());
        if (student.isPresent()) {
            student.get().addTimeIn(tinkleDto.getTimeIn());
            // student.get().addAverageDuration(tinkleDto.getAverageDuration());
            repository.save(student.get());
            return new ResponseEntity<>(student.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public List<Tinkle> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{person}")
    public ResponseEntity<Object> getTinkle(@PathVariable String name) {
        Optional<Tinkle> tinkle = repository.findByPersonName(name);
        if (tinkle.isPresent()) {
            Tinkle tinklePerson = tinkle.get();
            return new ResponseEntity<>(tinklePerson, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }
    }

}