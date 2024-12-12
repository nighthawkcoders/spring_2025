package com.nighthawk.spring_portfolio.mvc.calendar;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventsApiController {

    @Autowired
    private CalendarEventRepository eventRepository;

    @GetMapping("/calendar")
    public String calendar() {
        return "calendar";
    }

    @PostMapping("/add")
    public CalendarEvent addEvent(@RequestBody CalendarEvent event) {
        return eventRepository.save(event);
    }

    @GetMapping
    public List<CalendarEvent> getAllEvents() {
        return eventRepository.findAllByOrderByDateAsc();
    }
}