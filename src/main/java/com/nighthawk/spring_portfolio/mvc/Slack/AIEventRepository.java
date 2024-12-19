package com.nighthawk.spring_portfolio.mvc.Slack;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AIEventRepository extends JpaRepository<AIEvent, Long> {
    List<AIEvent> findByDate(LocalDate date);
    List<AIEvent> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<AIEvent> findByType(String type);
}