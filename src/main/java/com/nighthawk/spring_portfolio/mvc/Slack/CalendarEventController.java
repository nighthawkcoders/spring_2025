package com.nighthawk.spring_portfolio.mvc.Slack;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<Map<String, String>> addEvent(@RequestBody Map<String, String> jsonMap) {
        Map<String, String> response = new HashMap<>();
        try {
            // Validate required fields
            String title = jsonMap.get("title");
            String dateStr = jsonMap.get("date");

            if (title == null || title.trim().isEmpty()) {
                response.put("message", "Invalid input: 'title' cannot be null or empty.");
                return ResponseEntity.badRequest().body(response);
            }
            if (dateStr == null || dateStr.trim().isEmpty()) {
                response.put("message", "Invalid input: 'date' cannot be null or empty.");
                return ResponseEntity.badRequest().body(response);
            }

            // Parse the date string into LocalDate
            LocalDate date;
            try {
                date = LocalDate.parse(dateStr); // Ensure valid date format (YYYY-MM-DD)
            } catch (Exception e) {
                response.put("message", "Invalid date format. Use YYYY-MM-DD.");
                return ResponseEntity.badRequest().body(response);
            }

            // Get optional fields for description and type
            String description = jsonMap.getOrDefault("description", "");
            String type = jsonMap.getOrDefault("type", "general");

            // Create the event
            CalendarEvent event = new CalendarEvent(date, title, description, type);

            // Save the event using the service
            calendarEventService.saveEvent(event);

            // Return success response
            response.put("message", "Event added successfully.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Handle unexpected errors
            response.put("message", "Error adding event: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/events/{date}")
    public List<CalendarEvent> getEventsByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return calendarEventService.getEventsByDate(localDate);
    }

    @DeleteMapping("/delete/{title}")
    public ResponseEntity<String> deleteEvent(@PathVariable String title) {
        // Decode the title to handle multi-word or special character titles
        String decodedTitle = URLDecoder.decode(title, StandardCharsets.UTF_8);

        // Log the decoded title for debugging purposes
        System.out.println("Attempting to delete event with title: " + decodedTitle);

        try {
            // Call your service to delete the event
            boolean deleted = calendarEventService.deleteEventByTitle(decodedTitle);

            // If the event wasn't found and deleted, return a 404 response
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event with the given title not found.");
            }

            // Return a success response if the event is deleted
            return ResponseEntity.ok("Event deleted successfully.");
        } catch (Exception e) {
            // Log the exception and return a 500 error response
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        } 
    }


    @PutMapping("/edit/{title}")
    public ResponseEntity<String> editEvent(@PathVariable String title, @RequestBody Map<String, String> payload) {
        try {
         // Decode the title to handle multi-word or special character titles
            String decodedTitle = URLDecoder.decode(title, StandardCharsets.UTF_8);

            // Extract new title and description from payload
            String newTitle = payload.get("newTitle");
            String description = payload.get("description");

            // Validate input
            if (newTitle == null || newTitle.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("New title cannot be null or empty.");
            }
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Description cannot be null or empty.");
            }

           // Attempt to update the event
            boolean updated = calendarEventService.updateEventByTitle(decodedTitle, newTitle.trim(), description.trim());

            if (updated) {
                return ResponseEntity.ok("Event updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event with the given title not found.");
            }
        } catch (Exception e) {
            // Log the exception and return a proper error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the event: " + e.getMessage());
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


    @GetMapping("/events/next-day")
    public List<CalendarEvent> getNextDayEvents() {
        // Get the current date and the next day
        LocalDate today = LocalDate.now();
        LocalDate nextDay = today.plusDays(1);

        // Fetch events for the next day
        return calendarEventService.getEventsByDate(nextDay);
    }

}

