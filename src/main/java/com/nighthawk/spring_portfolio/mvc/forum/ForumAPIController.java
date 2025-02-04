package com.nighthawk.spring_portfolio.mvc.forum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.nighthawk.spring_portfolio.mvc.forum.ForumRepository;

@RestController
@RequestMapping("/forum")
public class ForumAPIController {
    
    @Autowired
    private ForumRepository forumRepository; // Inject the repository

    @PostMapping("/issue/post")
    public String getInput(@RequestBody RequestBodyData requestBodyData) {
        System.out.println("Received message: " + requestBodyData.getTitle());

        if (requestBodyData.getTitle() == null || requestBodyData.getTitle().isEmpty() || requestBodyData.getContext() == null || requestBodyData.getContext().isEmpty()) {
            return "Error: Title or Problem is required.";
        }
        if (requestBodyData.getAuthor() == null || requestBodyData.getAuthor().isEmpty()) {
            String[] authorEnding = {"Whale", "Pig", "Badger", "Warthog", "Fish", "Cow", "Chicken", "Rabbit", "Wolf", "Bear"};
            requestBodyData.setAuthor("Anonymous" + authorEnding[(int) (Math.random() * 10)]);
        }

        try {
            String title = requestBodyData.getTitle();
            String context = requestBodyData.getContext();
            String author = requestBodyData.getAuthor();

            // Create a new ForumTableController object and save it to the database
            Forum forumTable = new Forum(author, title, context);
            forumRepository.save(forumTable); // Use the repository to save

            return "Successfully added to database";
        } catch (RestClientException e) {
            System.out.println("An error occurred while generating text: " + e.getMessage());
            e.printStackTrace();
            return "An error occurred while generating text: " + e.getMessage();
        }
    }

    public static class RequestBodyData {
        private String title;
        private String context;
        private String author;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }
}
