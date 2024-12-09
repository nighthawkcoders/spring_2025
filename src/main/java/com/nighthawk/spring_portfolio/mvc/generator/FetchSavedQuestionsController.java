package com.nighthawk.spring_portfolio.mvc.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/saved-questions")
public class FetchSavedQuestionsController {

    @Autowired
    private GeneratedQuestionRepository questionRepository;

    @GetMapping
    public List<String> getSavedQuestions() {
        List<GeneratedQuestion> questions = questionRepository.findAll();
        return questions.stream()
                        .map(GeneratedQuestion::getQuestion)
                        .toList();
    }
}