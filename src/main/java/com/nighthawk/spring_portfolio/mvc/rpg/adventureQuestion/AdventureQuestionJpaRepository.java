package com.nighthawk.spring_portfolio.mvc.rpg.adventureQuestion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdventureQuestionJpaRepository extends JpaRepository<AdventureQuestion, Long> {
    AdventureQuestion findByTitle(String title); 
    AdventureQuestion findByContent(String content); 
    AdventureQuestion findById(Integer questionid);
    List<AdventureQuestion> findAllByOrderByTitleAsc();
}

