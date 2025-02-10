package com.nighthawk.spring_portfolio.mvc.forum.forumRankings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forum")
public class forumRanksAPI {

    @Autowired
    private forumRankRepository rankRepository; // Inject the repository

    @PostMapping("/rank/update")
    public String updateRank(@RequestBody RequestBodyData requestBodyData) {
        System.out.println("Received message: " + requestBodyData.getTitle());

        if (requestBodyData.getTitle() == null || requestBodyData.getTitle().isEmpty() || 
            requestBodyData.getContext() == null || requestBodyData.getContext().isEmpty()) {
            return "Error: Title or Problem is required.";
        }
        if (requestBodyData.getAuthor() == null || requestBodyData.getAuthor().isEmpty()) {
            return "Error: Author is required.";
        }

        try {
            String author = requestBodyData.getAuthor();
            int rankInt = requestBodyData.getRankInt();

            forumRankings currentRank = rankRepository.findByAuthor(author);
            if (currentRank != null) {
                currentRank.setRankInt(rankInt);
            } else {
                currentRank = new forumRankings(null, author, rankInt);
            }
            
            rankRepository.save(currentRank);
            return "Successfully added to database";
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }
    }

    public static class RequestBodyData {
        private String title;
        private String context;
        private String author;
        private int rankInt;

        // Getters and Setters
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

        public int getRankInt() {
            return rankInt;
        }

        public void setRankInt(int rankInt) {
            this.rankInt = rankInt;
        }
    }
}