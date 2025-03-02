package com.nighthawk.spring_portfolio.mvc.rpg.adventureChoice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AdventureChoiceJpaRepository extends JpaRepository<AdventureChoice, Long> {
    AdventureChoice findById(Integer choiceid);
}