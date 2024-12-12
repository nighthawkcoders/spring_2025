package com.nighthawk.spring_portfolio.mvc.rpg.question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJpaRepository extends JpaRepository<Question, Long> {
    Question findByTitle(String title); 
    Question findById(Integer questionid);
    List<Question> findAllByOrderByTitleAsc();
}

