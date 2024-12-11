package com.nighthawk.spring_portfolio.mvc.calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * This class provides RESTful API endpoints for managing Calendar events.
 * It includes endpoints for creating, retrieving, updating, and deleting Calendar events.
 */
@RestController
@RequestMapping("/api/calendar")
public class CalendarApiController {
    
    @Autowired
    private CalendarEventRepository repository; // Repository for accessing CalendarEvent entities in the database

    /**
     * Retrieves all Calendar events.
     *
     * @return A ResponseEntity containing a list of Calendar events.
     */
    @GetMapping
    public ResponseEntity<List<CalendarEvent>> getAllEvents() {
        List<CalendarEvent> events = repository.findAllByOrderByDateAsc(); // Custom method to get events ordered by date
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * Retrieves a Calendar event by its ID.
     *
     * @param id The ID of the Calendar event to retrieve.
     * @return A ResponseEntity containing the Calendar event if found, or a NOT_FOUND status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CalendarEvent> getEventById(@PathVariable long id) {
        Optional<CalendarEvent> optionalEvent = repository.findById(id);
        return optionalEvent.map(event -> new ResponseEntity<>(event, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Create a new Calendar event.
     *
     * @param calendarEvent The Calendar event to create.
     * @return A ResponseEntity containing the created Calendar event and a CREATED status.
     */
    @PostMapping
    public ResponseEntity<CalendarEvent> createEvent(@RequestBody CalendarEvent calendarEvent) {
        CalendarEvent createdEvent = repository.save(calendarEvent);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    /**
     * Updates an existing Calendar event.
     *
     * @param id The ID of the Calendar event to update.
     * @param calendarEvent The updated Calendar event data.
     * @return A ResponseEntity containing the updated Calendar event if successful, or a NOT_FOUND status if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CalendarEvent> updateEvent(@PathVariable long id, @RequestBody CalendarEvent calendarEvent) {
        if (!repository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        calendarEvent.setId(id); // Set the ID to ensure the existing record is updated
        CalendarEvent updatedEvent = repository.save(calendarEvent);
        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }

    /**
     * Deletes a Calendar event by its ID.
     *
     * @param id The ID of the Calendar event to delete.
     * @return A ResponseEntity containing a NO_CONTENT status if deleted, or a NOT_FOUND status if not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable long id) {
        if (!repository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}