package com.nighthawk.spring_portfolio.mvc.generator;

import org.springframework.beans.factory.annotation.Autowired; // Enables dependency injection
import org.springframework.web.bind.annotation.*; // REST API annotations

@RestController // Defines this class as a RESTful controller
@RequestMapping("/save-question") // Base URL path for this controller
public class SaveQuestionController {

    @Autowired // Automatically injects an instance of the repository
    private GeneratedQuestionRepository questionRepository;

    @PostMapping // Maps POST requests to this method
    public void saveQuestion(@RequestBody GeneratedQuestion question) {
        // Save the received question entity into the database
        questionRepository.save(question);
    }
}
