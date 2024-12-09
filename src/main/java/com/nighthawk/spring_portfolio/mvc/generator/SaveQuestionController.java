package com.nighthawk.spring_portfolio.mvc.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.nighthawk.spring_portfolio.mvc.generator.GeneratedQuestion;
import com.nighthawk.spring_portfolio.mvc.generator.GeneratedQuestionRepository;

@RestController
@RequestMapping("/save-question")
public class SaveQuestionController {

    @Autowired
    private GeneratedQuestionRepository questionRepository;

    @PostMapping
    public void saveQuestion(@RequestBody GeneratedQuestion question) {
        questionRepository.save(question);
    }
}
