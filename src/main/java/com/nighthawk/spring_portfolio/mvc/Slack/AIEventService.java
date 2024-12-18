package com.nighthawk.spring_portfolio.mvc.Slack;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AIEventService {

    @Autowired
    private AIEventRepository aiEventRepository;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = System.getenv("API_KEY"); // Fetch from environment variables


    // Save a new event
    public AIEvent saveEvent(AIEvent event) {
        return aiEventRepository.save(event);
    }

    // Retrieve all events
    public List<AIEvent> getAllEvents() {
        return aiEventRepository.findAll();
    }

    // Parse and save events using OpenAI GPT
    public void parseAndSaveEvents(Map<String, String> input) throws JsonMappingException, JsonProcessingException {
        String text = input.get("text");

        // Call OpenAI to process the text and return structured JSON
        String aiResponse = callOpenAI(text);
        List<AIEvent> events = parseAIResponse(aiResponse);

        // Save each event into the database
        for (AIEvent event : events) {
            saveEvent(event);
        }
    }

    // Helper method to call OpenAI API
    private String callOpenAI(String text) {
        RestTemplate restTemplate = new RestTemplate();

        // Prepare OpenAI request payload
        Map<String, Object> requestPayload = Map.of(
            "model", "gpt-4", // Specify the model
            "messages", List.of(
                Map.of("role", "system", "content", "You are a data parser. Extract structured information in JSON format from the following text:"),
                Map.of("role", "user", "content", text)
            ),
            "temperature", 0.0
        );

        // Prepare HTTP headers
        Map<String, String> headers = Map.of(
            "Authorization", "Bearer " + API_KEY,
            "Content-Type", "application/json"
        );

        // Send POST request to OpenAI API
        try {
            return restTemplate.postForObject(
                OPENAI_API_URL,
                Map.of("headers", headers, "body", requestPayload),
                String.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Error calling OpenAI API: " + e.getMessage());
        }
    }

    // Parse OpenAI API response into AIEvent objects
    private List<AIEvent> parseAIResponse(String aiResponse) throws JsonMappingException, JsonProcessingException {
        List<AIEvent> events = new ArrayList<>();

        // Parse JSON from the response (pseudo-code, replace with actual JSON library usage)
        // Example of AI response:
        // {
        //   "events": [
        //     {"date": "2023-12-06", "title": "Be sure you are signed up", "description": "Make a realistic plan today...", "type": "daily plan"},
        //     {"date": "2023-12-07", "title": "Work days", "description": "Check in with person...", "type": "check-in"}
        //   ]
        // }
        Map<String, Object> parsedResponse = new ObjectMapper().readValue(aiResponse, Map.class);
        List<Map<String, String>> eventList = (List<Map<String, String>>) parsedResponse.get("events");

        for (Map<String, String> eventData : eventList) {
            LocalDate date = LocalDate.parse(eventData.get("date"));
            String title = eventData.get("title");
            String description = eventData.get("description");
            String type = eventData.get("type");
            events.add(new AIEvent(date, title, description, type));
        }

        return events;
    }
}
