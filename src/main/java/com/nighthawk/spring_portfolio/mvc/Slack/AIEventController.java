package com.nighthawk.spring_portfolio.mvc.Slack;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping("/api/ai/calendar")
public class AIEventController {

    @Autowired
    private AIEventService aiEventService;

    @PostMapping("/add_event")
    public void addEvents(@RequestBody Map<String, String> input) throws JsonMappingException, JsonProcessingException {
        aiEventService.parseAndSaveEvents(input);
    }

    @GetMapping("/events")
    public List<AIEvent> getAllEvents() {
        return aiEventService.getAllEvents();
    }
}
