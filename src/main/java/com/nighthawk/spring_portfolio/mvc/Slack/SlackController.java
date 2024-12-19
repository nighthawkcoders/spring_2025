package com.nighthawk.spring_portfolio.mvc.Slack;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;

@RestController
public class SlackController {
    Dotenv dotenv = Dotenv.load();
    private final String slackToken = dotenv.get("SLACK_TOKEN");
    private final RestTemplate restTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SlackMessageRepository messageRepository;

    // Constructor
    public SlackController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Deprecated feature sadly
    // Was used to get a list of every slack message
    @GetMapping("/slack/")
    public ResponseEntity<List<SlackMessage>> returnSlackData() {
        List<SlackMessage> messages = messageRepository.findAll();
        return ResponseEntity.ok(messages);
    }

    // Deprecated feature sadly
    // Was used to link the user id of the message sender to their actual name
    @PostMapping("/slack/getUsername")
    public ResponseEntity<String> getUsername(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.get("userId");
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user ID");
        }

        String url = "https://slack.com/api/users.info?user=" + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + slackToken);
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null || !(boolean) body.get("ok")) {
            return ResponseEntity.status(400).body("Failed to fetch user info");
        }

        Map<String, Object> user = (Map<String, Object>) body.get("user");
        String username = (String) ((Map<String, Object>) user.get("profile")).get("real_name");

        return ResponseEntity.ok(username);
    }

    // Main message receiver function
    @PostMapping("/slack/events")
    public ResponseEntity<String> handleSlackEvent(@RequestBody SlackEvent payload) {
        if (payload.getChallenge() != null) {
            return ResponseEntity.ok(payload.getChallenge());
        }
    
        try {
            SlackEvent.Event messageEvent = payload.getEvent();
            String eventType = messageEvent.getType();
    
            // Distinguishing messages from other events
            if ("message".equals(eventType)) {
                ObjectMapper objectMapper = new ObjectMapper();
                String messageContent = objectMapper.writeValueAsString(messageEvent);

                // Save the message to the database
                messageService.saveMessage(messageContent);
                System.out.println("Message saved to database: " + messageContent);

                // Send the message to the calendar service
                String calendarUrl = "http://localhost:8085/api/calendar/add";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");

                HttpEntity<String> calendarEntity = new HttpEntity<>(messageContent, headers);
                ResponseEntity<String> calendarResponse = restTemplate.postForEntity(calendarUrl, calendarEntity, String.class);

                System.out.println("Message sent to calendar service: " + calendarResponse.getBody());

                // Send the message to the AI calendar service
                // sendToAICalendar(messageContent);
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return ResponseEntity.ok("OK");
    }

    private void sendToAICalendar(String messageContent) {
        try {
            // Prepare the AI calendar service URL
            String aiCalendarUrl = "http://localhost:8085/api/ai/calendar/add_event";

            // Create headers for the AI calendar request
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // Create the request entity with the message content
            HttpEntity<String> aiCalendarEntity = new HttpEntity<>(messageContent, headers);

            // Send the request to the AI calendar service
            ResponseEntity<String> aiCalendarResponse = restTemplate.postForEntity(aiCalendarUrl, aiCalendarEntity, String.class);

            System.out.println("Message sent to AI calendar service: " + aiCalendarResponse.getBody());
        } catch (Exception e) {
            System.err.println("Failed to send message to AI calendar service.");
            e.printStackTrace();
        }
    }
}
