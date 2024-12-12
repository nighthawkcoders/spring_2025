package com.nighthawk.spring_portfolio.hacks.calendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * Calendar API Controller 
 * 
 * Provides endpoints to determine if a year is a leap year and to retrieve events.
 */
@RestController
@RequestMapping("/api/calendar")
public class HackCalendarApiController {

    /** 
     * GET endpoint to check if a year is a leap year.
     * 
     * @param year the year to check
     * @return ResponseEntity containing JSON with year and leap year status
     * @throws JsonProcessingException if there is an error processing JSON
     * @throws JsonMappingException if there is an error mapping JSON
     */
    @GetMapping("/isLeapYear/{year}")
    public ResponseEntity<JsonNode> getIsLeapYear(@PathVariable int year) 
            throws JsonMappingException, JsonProcessingException {
        
        // Create a Year object and evaluate if it's a leap year
        Year yearObj = new Year();
        yearObj.setYear(year);
        
        // Convert Year object to JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(yearObj.isLeapYearToString());
        
        return ResponseEntity.ok(jsonResponse);  // Return JSON response
    }

    /** 
     * GET endpoint to retrieve a list of events.
     * 
     * @return ResponseEntity containing a list of events in JSON format
     */
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getEvents() {
        // Create a sample list of events
        List<Event> events = new ArrayList<>();
        events.add(new Event("Team Meeting", "Discuss project updates", LocalDate.of(2024, 11, 1)));
        events.add(new Event("Annual Conference", "Annual conference for developers", LocalDate.of(2024, 11, 15)));
        events.add(new Event("Product Launch", "Launch of the new product line", LocalDate.of(2024, 12, 5)));

        // Return JSON response with the correct Content-Type
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")  // Set Content-Type
                .body(events);
    }

    // Additional methods can be added here as needed
}
