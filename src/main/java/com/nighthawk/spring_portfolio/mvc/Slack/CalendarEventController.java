package com.nighthawk.spring_portfolio.mvc.Slack;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendar")
public class CalendarEventController {

    @Autowired
    private CalendarEventService calendarEventService;

    @PostMapping("/add")
    public void addEventsFromSlackMessage(@RequestBody Map<String, String> jsonMap) {
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = String.format("%d-%02d-%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        LocalDate weekStartDate = LocalDate.parse(formattedDate);
        //Gets the current day including the month, and parses it through the parseSlackMessage method
        calendarEventService.parseSlackMessage(jsonMap, weekStartDate);
    }
    
    @PostMapping("/add_event")
    public void addEvents(@RequestBody Map<String, String> jsonMap) {
        if (jsonMap.containsKey("text")) {
            // Parse Slack message if "text" key exists
            LocalDateTime now = LocalDateTime.now();
            String formattedDate = String.format("%d-%02d-%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
            LocalDate weekStartDate = LocalDate.parse(formattedDate);
            calendarEventService.parseSlackMessage(jsonMap, weekStartDate);
        } else if (jsonMap.containsKey("date") && jsonMap.containsKey("title")) {
            // Add single event manually if "date" and "title" keys exist
            LocalDate date = LocalDate.parse(jsonMap.get("date"));
            String title = jsonMap.get("title");
            String description = jsonMap.getOrDefault("description", "");
            String type = jsonMap.getOrDefault("type", "general"); 
            CalendarEvent event = new CalendarEvent(date, title, description, type);
            calendarEventService.saveEvent(event);
        } else {
            throw new IllegalArgumentException("Invalid input: Must include either 'text' for Slack messages or 'date' and 'title' for single event addition.");
        }
    }

    @GetMapping("/events/{date}")
    public List<CalendarEvent> getEventsByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return calendarEventService.getEventsByDate(localDate);
    }

    @PutMapping("/edit/{title}")
    public ResponseEntity<String> editEvent(@PathVariable String title, @RequestBody Map<String, String> payload) {
        // Decode the title to handle multi-word or special character titles
        String decodedTitle = URLDecoder.decode(title, StandardCharsets.UTF_8);
        String newTitle = payload.get("newTitle");
        String description = payload.get("description");

        if (newTitle == null || newTitle.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("New title cannot be null or empty.");
        }
        if (description == null || description.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Description cannot be null or empty.");
        }

        try {
            boolean updated = calendarEventService.updateEventByTitle(decodedTitle, newTitle, description);
            if (!updated) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event with the given title not found.");
            }   
            return ResponseEntity.ok("Event updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/events")
    public List<CalendarEvent> getAllEvents() {
        return calendarEventService.getAllEvents();
    }

    @GetMapping("/events/range")
    public List<CalendarEvent> getEventsWithinDateRange(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return calendarEventService.getEventsWithinDateRange(startDate, endDate);
    }
}
