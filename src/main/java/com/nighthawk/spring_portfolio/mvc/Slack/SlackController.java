package com.nighthawk.spring_portfolio.mvc.Slack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Map;
import java.util.List;

@RestController
public class SlackController {
    Dotenv dotenv = Dotenv.load();
    private String slackToken = dotenv.get("SLACK_TOKEN");
    private final RestTemplate restTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SlackMessageRepository messageRepository;

    public SlackController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/slack/")
    public ResponseEntity<List<SlackMessage>> returnSlackData() {
        List<SlackMessage> messages = messageRepository.findAll();
        return ResponseEntity.ok(messages);
    }

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

    @PostMapping("/slack/events")
    public ResponseEntity<String> handleSlackEvent(@RequestBody SlackEvent payload) {
        if (payload.getChallenge() != null) {
            return ResponseEntity.ok(payload.getChallenge());
        }

        try {
            SlackEvent.Event messageEvent = payload.getEvent();
            String eventType = messageEvent.getType();

            if ("message".equals(eventType)) {
                ObjectMapper objectMapper = new ObjectMapper();
                String messageContent = objectMapper.writeValueAsString(messageEvent);

                messageService.saveMessage(messageContent);

                System.out.println("Message saved to database: " + messageContent);

                String calendarUrl = "http://localhost:8085/api/calendar/add";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");

                HttpEntity<String> calendarEntity = new HttpEntity<>(messageContent, headers);
                ResponseEntity<String> calendarResponse = restTemplate.postForEntity(calendarUrl, calendarEntity, String.class);

                System.out.println("Message sent to calendar service: " + calendarResponse.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("OK");
    }
}
