package com.nighthawk.spring_portfolio.mvc.Slack;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByDate(LocalDate date);
    List<CalendarEvent> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<CalendarEvent> findByType(String type);
}