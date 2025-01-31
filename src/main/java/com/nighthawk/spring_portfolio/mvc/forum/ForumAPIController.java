package com.nighthawk.spring_portfolio.mvc.forum;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;



@RestController
@RequestMapping("/forum")
public class ForumAPIController {
    @PostMapping("/input")
    public String getInput(@RequestBody RequestBodyData requestBodyData) {
        System.out.println("Received message: " + requestBodyData.getTitle());
        if (requestBodyData.getTitle() == null || requestBodyData.getTitle().isEmpty()) {
            return "Error: code_block is required.";
        }
        try {
            String response = requestBodyData.getTitle();
            return response;
        } catch (RestClientException e) {
            System.out.println("An error occurred while generating text: " + e.getMessage());
            e.printStackTrace();
            return "An error occurred while generating text: " + e.getMessage();
        }
    }

    public static class RequestBodyData {
        private String title;
        // private String content;
        // private String author;

        public String getTitle() {
            return title;
        }
    }
}
