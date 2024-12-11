package com.nighthawk.spring_portfolio.mvc.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Logging utility for debugging and tracking events
import org.springframework.boot.SpringApplication; // Bootstraps the Spring Boot application
import org.springframework.http.HttpEntity; // Represents an HTTP request/response entity
import org.springframework.http.HttpHeaders; // Manages HTTP request headers
import org.springframework.http.HttpMethod; // Enum for HTTP methods
import org.springframework.http.HttpStatus; // Represents HTTP status codes
import org.springframework.http.ResponseEntity; // Encapsulates HTTP response
import org.springframework.web.bind.annotation.*; // REST API annotations
import org.springframework.web.client.RestTemplate; // Simplifies HTTP request execution

import com.fasterxml.jackson.databind.JsonNode; // Parses JSON into a tree-like structure
import com.fasterxml.jackson.databind.ObjectMapper; // Converts JSON to Java objects and vice versa

@RestController // Defines this class as a RESTful controller
@RequestMapping("/generate") // Base URL path for this controller
@CrossOrigin(origins = "*") // Enables cross-origin requests from any domain
public class Generator {

    private static final Logger logger = LoggerFactory.getLogger(Generator.class); // Logger for this class
    
    private static final String GROQ_API_KEY = "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"; // API key for authentication
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions"; // Endpoint for the external API

    public static void main(String[] args) {
        SpringApplication.run(Generator.class, args); // Starts the Spring Boot application
    }

    @PostMapping("/question") // Maps POST requests to this method
    public ResponseEntity<String> generateQuestion(@RequestBody UserRequest userRequest) {
        // Log the incoming request details
        logger.info("Received request to generate question for topic: {}", userRequest.getTopic());
        
        // Create the question prompt based on user input
        String prompt = createPrompt(userRequest);
        
        // Generate the question using the external API
        String generatedQuestion = callGroqAPI(prompt);
        
        // Return the generated question as a response
        return ResponseEntity.ok(generatedQuestion);
    }

    // Method to create a prompt for the external API based on user input
    private String createPrompt(UserRequest userRequest) {
        StringBuilder prompt = new StringBuilder(); // StringBuilder for efficient string manipulation
        prompt.append("Generate a question about ").append(userRequest.getTopic()).append(". ");
        
        // Check if the requirements include "mc" (multiple-choice question)
        if (userRequest.getRequirements().toLowerCase().contains("mc")) {
            prompt.append("Make it a multiple-choice question with four options (A, B, C, D) and one correct answer.");
        } else {
            // Build a free-response question prompt
            prompt.append("Don't explain why you made the quesiton, don't give a title such as here's a question about blah blah. just ask the question. The question should guide students to write a code block or free response based on these requirements: ")
                  .append(userRequest.getRequirements()).append(". ");
        }
        
        // Append any additional formatting instructions
        prompt.append("Format the question according to these instructions: ").append(userRequest.getRequirements()).append(".");
        return prompt.toString();
    }

    // Method to call the external API with the generated prompt
    private String callGroqAPI(String prompt) {
        RestTemplate restTemplate = new RestTemplate(); // Simplifies HTTP requests
        
        // Prepare HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json"); // Set content type to JSON
        headers.set("Authorization", "Bearer " + GROQ_API_KEY); // Add the API key for authorization
        
        // Prepare the request body with the prompt
        String requestBody = String.format("{\"model\": \"llama3-8b-8192\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}", prompt);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers); // Encapsulates the request
        
        // Execute the POST request to the external API
        try {
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, String.class);
            
            // If the API call is successful, parse and return the response content
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper(); // JSON parser
                JsonNode jsonNode = objectMapper.readTree(response.getBody()); // Parse the response body
                return jsonNode.get("choices").get(0).get("message").get("content").asText(); // Extract the generated text
            } else {
                // Log and return an error message if the API response is not OK
                logger.error("Error calling Groq API: {}", response.getStatusCode());
                return "Error: " + response.getStatusCode();
            }
        } catch (Exception e) {
            // Log and return an error message if an exception occurs
            logger.error("Exception while calling Groq API: {}", e.getMessage());
            return "Error calling Groq API.";
        }
    }
}

// Class to encapsulate the user's input
class UserRequest {
    private String topic; // Topic for the question
    private String requirements; // Question format and other requirements

    // Getter and setter for 'topic'
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    // Getter and setter for 'requirements'
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
}
