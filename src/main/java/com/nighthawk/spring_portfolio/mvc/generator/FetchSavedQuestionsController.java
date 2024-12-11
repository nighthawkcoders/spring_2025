package com.nighthawk.spring_portfolio.mvc.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// REST controller to handle saved questions API
@RestController
@RequestMapping("/saved-questions")
public class FetchSavedQuestionsController {

    // Repository for interacting with the database
    @Autowired
    private GeneratedQuestionRepository questionRepository;

    // Endpoint to retrieve all saved questions
    @GetMapping
    public List<String> getSavedQuestions() {
        // Fetch all questions from the repository
        List<GeneratedQuestion> questions = questionRepository.findAll();

        // Convert the list of GeneratedQuestion objects to a list of question strings
        return questions.stream()
                        .map(GeneratedQuestion::getQuestion)
                        .toList();
    }
}
