package com.nighthawk.spring_portfolio.mvc.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/generate")
@CrossOrigin(origins = "*")
public class Generator {

    private static final Logger logger = LoggerFactory.getLogger(Generator.class);
    
    private static final String GROQ_API_KEY = "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"; 
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions"; 

    public static void main(String[] args) {
        SpringApplication.run(Generator.class, args);
    }

    @PostMapping("/question")
    public ResponseEntity<String> generateQuestion(@RequestBody UserRequest userRequest) {
        logger.info("Received request to generate question for topic: {}", userRequest.getTopic());
        String prompt = createPrompt(userRequest);
        String generatedQuestion = callGroqAPI(prompt);
        return ResponseEntity.ok(generatedQuestion);
    }

    private String createPrompt(UserRequest userRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a question about ").append(userRequest.getTopic()).append(". ");
        
        if (userRequest.getRequirements().toLowerCase().contains("mc")) {
            prompt.append("Make it a multiple-choice question with four options (A, B, C, D) and one correct answer.");
        } else {
            prompt.append("Don't explain why you made the quesiton, don't give a title such as here's a question about blah blah. just ask the question. The question should guide students to write a code block or free response based on these requirements: ")
                  .append(userRequest.getRequirements()).append(". ");
        }
        
        prompt.append("Format the question according to these instructions: ").append(userRequest.getRequirements()).append(".");
        return prompt.toString();
    }

    private String callGroqAPI(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        
        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + GROQ_API_KEY);
        
        // Prepare request body
        String requestBody = String.format("{\"model\": \"llama3-8b-8192\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}", prompt);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        
        // Call the API
        try {
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("choices").get(0).get("message").get("content").asText();
            } else {
                logger.error("Error calling Groq API: {}", response.getStatusCode());
                return "Error: " + response.getStatusCode();
            }
        } catch (Exception e) {
            logger.error("Exception while calling Groq API: {}", e.getMessage());
            return "Error calling Groq API.";
        }
    }
}

// Model for user input
class UserRequest {
    private String topic;
    private String requirements;

    // Getters and Setters
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
}