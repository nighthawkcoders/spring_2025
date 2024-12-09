package com.nighthawk.spring_portfolio.mvc.messages;

import com.nighthawk.spring_portfolio.mvc.person.Person;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;
import com.nighthawk.spring_portfolio.mvc.person.PersonRole;
import com.nighthawk.spring_portfolio.mvc.person.PersonRoleJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://127.0.0.1:4100", allowCredentials = "true")
public class CommentApiController {

     private static final Logger logger = LoggerFactory.getLogger(CommentApiController.class);
    @Autowired
    private CommentJpaRepository commentRepository;

    @Autowired
    private MessageJpaRepository messageRepository;

      @Autowired
    private PersonJpaRepository personRepository;

    @Autowired
    private PersonRoleJpaRepository personRoleRepository; // For role lookup

    @GetMapping
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @PostMapping("/{messageId}")
    public ResponseEntity<Comment> createComment(@PathVariable Long messageId, @RequestBody Comment comment) {
        return messageRepository.findById(messageId).map(message -> {
            comment.setMessage(message);
            return ResponseEntity.ok(commentRepository.save(comment));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        if (commentRepository.existsById(id)) {
            logger.info("Comment exists, attempting to delete...");
            commentRepository.deleteById(id);
            logger.info("Comment deleted successfully.");
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
