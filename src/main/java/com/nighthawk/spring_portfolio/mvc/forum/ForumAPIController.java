package com.nighthawk.spring_portfolio.mvc.forum;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

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

            // check if the title is already in the database
            if (forumRepository.findByTitle(title) != null) {
                return "Error: Title already exists.";
            }
            
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


    @GetMapping("/{id}")
        public ResponseEntity<Forum> getPostById(@PathVariable Long id) {
        Optional<Forum> post = forumRepository.findById(id);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get")
    public List<Forum> getAllPosts() {
        return forumRepository.findAll();
    }


    @PostMapping("/blog/remove")
    public String removeBlog(@RequestBody RequestBlogData requestBlogData) {
        System.out.println("Received message: " + requestBlogData.getTitle());
        try {
            String title = requestBlogData.getTitle();
            String author = requestBlogData.getAuthor();

            String fileName = title.replaceAll(" ", "_").toLowerCase() + ".txt";
            String filePath = "volumes/forumBlogs/" + fileName;
            // delete the body to the file
            java.nio.file.Files.delete(java.nio.file.Paths.get(filePath));
            return "Successfully removed blog from database";
        } catch (RestClientException | IOException e) {
            System.out.println("An error occurred while writing the blog: " + e.getMessage());
            e.printStackTrace();
            return "An error occurred while writing the blog: " + e.getMessage();
        }
    }

    @PostMapping("/blog/post")
    public String getInputBlog(@RequestBody RequestBlogData requestBlogData) {
        System.out.println("Received message: " + requestBlogData.getTitle());
        try {
            String title = requestBlogData.getTitle();
            String body = requestBlogData.getBody();
            String author = requestBlogData.getAuthor();

            String fileName = title.replaceAll(" ", "_").toLowerCase() + ".txt";
            String filePath = "volumes/forumBlogs/" + fileName;
            // check if the title is already in the database
            if (forumRepository.findByTitle(title) != null) {
                return "Error: Title already exists.";
            }
            if (author == null || author.isEmpty()) {
                String[] authorEnding = {"Whale", "Pig", "Badger", "Warthog", "Fish", "Cow", "Chicken", "Rabbit", "Wolf", "Bear"};
                requestBlogData.setAuthor("Anonymous" + authorEnding[(int) (Math.random() * 10)]);
            }
            // save the body to the file
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), body.getBytes());
            return "Successfully added blog to database";
        } catch (RestClientException | IOException e) {
            System.out.println("An error occurred while writing the blog: " + e.getMessage());
            e.printStackTrace();
            return "An error occurred while writing the blog: " + e.getMessage();
        }
    }



    public static class RequestBlogData {
        private String title;
        private String body;
        private String author;

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getBody() {
            return body;
        }
        public void setBody(String body) {
            this.body = body;
        }
        public void setAuthor(String author) {
            this.author = author;
        }
        public String getAuthor() {
            return author;
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
